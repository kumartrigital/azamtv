Drop procedure getparent;

DELIMITER $$
CREATE PROCEDURE getparent(IN office_id INT, OUT path TEXT)
BEGIN
    DECLARE officename VARCHAR(200);
    DECLARE temppath TEXT;
    DECLARE tempparent INT;
    SET max_sp_recursion_depth = 255;
    SELECT name, parent_id FROM m_office WHERE id=office_id INTO officename, tempparent;
    IF tempparent IS NULL
    THEN
        SET path = officename;
    ELSE
        CALL getparent(tempparent, temppath);
        SET path = CONCAT(officename, '->', temppath);
    END IF;
END $$
DELIMITER ;
