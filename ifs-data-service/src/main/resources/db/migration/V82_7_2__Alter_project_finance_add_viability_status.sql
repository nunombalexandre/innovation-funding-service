ALTER TABLE project_finance ADD COLUMN `viability_status` ENUM('UNSET', 'GREEN', 'AMBER', 'RED') NULL DEFAULT NULL';