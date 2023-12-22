CREATE DATABASE SCOPED CREDENTIAL AzureStorageCredential2
WITH IDENTITY='pocgendata',
SECRET = '0rky3N+pAaEnMItRZlQuJ51QficbQhEYCLDmNzKfQ4GjAeOC9yyjYbJ9mUM8fHzvHcTa817Ky0vS+ASt7fckxA=='

CREATE EXTERNAL DATA SOURCE AzureDataLakeStore2
WITH
  -- Please note the abfss endpoint when your account has secure transfer enabled
  ( LOCATION = 'abfss://demo@pocgendata.dfs.core.windows.net/' ,
    CREDENTIAL = AzureStorageCredential2 ,
    TYPE = HADOOP
  ) ;

CREATE EXTERNAL FILE FORMAT TextFileFormat WITH
(
    FORMAT_TYPE=DELIMITEDTEXT,
    FORMAT_OPTIONS(
        FIELD_TERMINATOR=',',
        FIRST_ROW=2
    )
)

CREATE EXTERNAL TABLE [logdata]
(
    [Correlation id] [VARCHAR](200) NULL,
    [Operation name] [VARCHAR](200) NULL,
    [Status] [VARCHAR](100) NULL,
    [Event category] [VARCHAR](100) NULL,
    [Level] [VARCHAR](100) NULL,
    [Time] [DATETIME] NULL,
    [Subscription] [VARCHAR](200) NULL,
    [Event Initiated by] [VARCHAR](100) NULL,
    [Resource Type] [VARCHAR](1000) NULL,
    [Resource Group] [VARCHAR](1000) NULL,
    [Resource] [VARCHAR](2000) NULL
)
WITH(
    LOCATION = 'logdata/Log.csv',
    DATA_SOURCE = AzureDataLakeStore2,
    FILE_FORMAT = TextFileFormat
)

SELECT * from [logdata]

select * from [logdata]
