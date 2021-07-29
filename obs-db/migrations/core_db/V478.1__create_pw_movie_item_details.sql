CREATE TABLE IF NOT EXISTS `obstenant-default`.`pw_movie_item_details` (
`movie_name` VARCHAR(45) NOT NULL,
`ngb_item_id` VARCHAR(45) NULL,
`offer_Id`    VARCHAR(45) NULL,
`item_code` VARCHAR(45) NULL,
`item_type` VARCHAR(45) NULL,
`cms_item_code` VARCHAR(45) NULL,
`item_description` VARCHAR(45) NULL,
`price` DOUBLE NULL,
`Status` VARCHAR(45) NULL,
`status_descr` VARCHAR(105) NULL,
PRIMARY KEY (`movie_name`));
