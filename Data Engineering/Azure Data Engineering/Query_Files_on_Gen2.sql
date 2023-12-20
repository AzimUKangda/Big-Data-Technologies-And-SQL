select * FROM
OPENROWSET(BULK 'https://pocgendata.blob.core.windows.net/demo/parquet/*.parquet',
FORMAT='parquet') as [logdata_tmp]
