package com.indfinvestor.app.nav.amfi.bulkload;

import com.indfinvestor.app.nav.model.dto.MfNavDetails;
import com.indfinvestor.app.nav.model.dto.MfNavRecord;
import com.indfinvestor.app.nav.model.dto.MfSchemeDetailsRecord;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.nio.charset.Charset;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.batch.item.ItemReader;

@Slf4j
public class AmfiFileItemReader implements ItemReader<MfNavDetails> {

    private final String name;
    private final String fileName;
    private final Charset encoding;
    private boolean noInput = false;

    private static final String SCHEME_TYPE_OPEN_ENDED = "Open Ended Schemes";
    private static final String MUTUAL_FUND = " Mutual Fund";

    public AmfiFileItemReader(String name, String fileName, Charset encoding) {
        this.name = name;
        this.fileName = fileName;
        this.encoding = encoding;
    }

    @Override
    public MfNavDetails read() throws IOException {

        if (noInput) {
            return null;
        }

        MfNavDetails mfNavDetails = new MfNavDetails();

        var result = new HashMap<MfSchemeDetailsRecord, List<MfNavRecord>>();
        var category = "";
        var subCategory = "";
        var content = FileUtils.readFileToString(new File(fileName), encoding);
        try (var bufferedReader = new BufferedReader(new StringReader(content))) {

            String line;
            while ((line = bufferedReader.readLine()) != null) {
                // Set category
                if (line.contains(SCHEME_TYPE_OPEN_ENDED)) {
                    var rePattern = "\\((.*)\\)";
                    var p = Pattern.compile(rePattern);
                    var m = p.matcher(line);
                    if (m.find()) {
                        var extracted = m.group(1);
                        var split = extracted.split("-");
                        log.debug("Split {}", split);
                        if (split.length == 1) {
                            category = "Other";
                            subCategory = split[0].trim();
                            log.debug("Category:{} subCategory:{}", category, subCategory);
                        } else {
                            category = split[0].trim();
                            subCategory = split[1].trim();
                            log.debug("Category:{} subCategory:{}", category, subCategory);
                        }
                    }
                }

                if (StringUtils.isNotBlank(category) && StringUtils.isNotBlank(subCategory)) {
                    if (line.contains(";")) {
                        var splitRow = line.split(";");

                        var formatter = DateTimeFormatter.ofPattern("dd-MMM-yyyy", Locale.ENGLISH);
                        var navDate = LocalDate.parse(splitRow[7], formatter);
                        var schemeCode = splitRow[0].trim();
                        var schemeName = splitRow[1].trim();
                        var key = new MfSchemeDetailsRecord(schemeCode, schemeName, category, subCategory);
                        if ((schemeName.toUpperCase().contains("GROWTH")
                                        && !schemeName.toUpperCase().contains("INSTITUTIONAL"))
                                || schemeName.contains("Reliance")) {
                            var nav = splitRow[4].trim();

                            if (NumberUtils.isParsable(nav)) {
                                var record =
                                        new MfNavRecord(schemeCode, schemeName, nav, navDate, category, subCategory);
                                if (result.containsKey(key)) {
                                    var navRecords = result.get(key);
                                    navRecords.add(record);
                                } else {
                                    var navRecords = new ArrayList<>(List.of(record));
                                    result.put(key, navRecords);
                                }
                            }
                        }
                    } else if (line.endsWith(MUTUAL_FUND)) {
                        mfNavDetails.setFundHouse(line.trim());
                    }
                }
            }

        } catch (IOException e) {
            noInput = true;
            throw new RuntimeException(e);
        }

        mfNavDetails.setHistoricalNavData(result);
        noInput = true;
        return mfNavDetails;
    }
}
