Select msd.scheme_name, msd.category, msd.sub_category, stat.*
from mf_return_stats stat
         inner join public.mf_scheme_details msd on msd.id = stat.scheme_id;