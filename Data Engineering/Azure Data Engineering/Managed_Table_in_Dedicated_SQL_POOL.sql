CREATE TABLE [dbo].[Table]
(
    col1 int NOT NULL
)
WITH
(
    DISTRIBUTION = HASH (col1),
    CLUSTERED COLUMNSTORE INDEX
)
GO

INSERT INTO [dbo].[Table] VALUES (2);

SELECT * from [dbo].[Table];

--command to describe managed
EXEC sp_columns  [Table]
