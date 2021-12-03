select concat(bank, branch, lpad(cust, 8, "0"), acnt_num) as account_number,
cast(interest_rate as decimal(10, 4)) / cast(100 as decimal(10, 4)) as interest_rate,
date_format(to_date(start_Date, 'yyyyMMdd'), 'yyyy-MM-dd') as date from quality_data;