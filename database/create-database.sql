-- Mo file nay trong SSMS (File > Open > File), bam Execute (F5).
-- Chi can chay 1 lan: tao database va dat mat khau cho user sa.

IF DB_ID('CourseDb') IS NULL
    CREATE DATABASE CourseDb;
GO

-- Bat user sa va dat mat khau (phai trung voi spring.datasource.password trong application.properties)
ALTER LOGIN sa ENABLE;
ALTER LOGIN sa WITH PASSWORD = 'Abc@12345', CHECK_POLICY = OFF;
GO
