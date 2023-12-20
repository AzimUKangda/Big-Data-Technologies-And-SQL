CREATE DATABASE [appdb]

CREATE MASTER KEY ENCRYPTION BY PASSWORD = 'P@ssword@123'

CREATE DATABASE SCOPED CREDENTIAL SasTocken
WITH IDENTITY='SHARED ACCESS SIGNATURE'
, SECRET = '?sv=2022-11-02&ss=bfqt&srt=sco&sp=rwdlacupyx&se=2023-12-17T20:43:05Z&st=2023-12-17T12:43:05Z&spr=https&sig=1ObcOp%2FRoipDo4EA7u3e9eDGb2VTfTcMPrPVb2pvK6I%3D'

CREATE EXTERNAL DATA SOURCE log_data
with (LOCATION='https://pocgendata.blob.core.windows.net/demo/log-1',
      CREDENTIAL=SasTocken)

CREATE EXTERNAL FILE FORMAT TextFileFormat WITH
(
    FORMAT_TYPE=DELIMITEDTEXT,
    FORMAT_OPTIONS(
        FIELD_TERMINATOR=',',
        FIRST_ROW=2
    )
)
DROP EXTERNAL TABLE [logdata]

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
    LOCATION = '/Log.csv',
    DATA_SOURCE = log_data,
    FILE_FORMAT = TextFileFormat
)

select * from [logdata]
