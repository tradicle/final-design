-- Check if category column exists before adding
SET @s = (SELECT IF(
  (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS
   WHERE TABLE_SCHEMA = 'animal_db' AND TABLE_NAME = 'knowledge' AND COLUMN_NAME = 'category') = 0,
  'ALTER TABLE knowledge ADD COLUMN category VARCHAR(50) DEFAULT NULL COMMENT ''分类''',
  'SELECT ''category column already exists'''
));
PREPARE stmt FROM @s;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;
