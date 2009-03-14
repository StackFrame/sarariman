CREATE TABLE IF NOT EXISTS tasks (id INT UNSIGNED NOT NULL AUTO_INCREMENT,
                                  PRIMARY KEY (id),
                                  name TEXT CHARACTER SET utf8 NOT NULL,
		                  billable BOOLEAN NOT NULL,
		                  description TEXT CHARACTER SET utf8,
		                  active BOOLEAN NOT NULL) ENGINE=INNODB;

CREATE TABLE IF NOT EXISTS hours (employee INT UNSIGNED NOT NULL,
                                  task INT UNSIGNED NOT NULL,
                                  date DATE NOT NULL,
		                  PRIMARY KEY (employee, task, date),
		                  FOREIGN KEY (task) REFERENCES tasks(id),
		                  description TEXT CHARACTER SET utf8,
                                  duration DECIMAL (4,2) UNSIGNED NOT NULL) ENGINE=INNODB;

CREATE TABLE IF NOT EXISTS hours_changelog (employee INT UNSIGNED NOT NULL,
                                            task INT UNSIGNED NOT NULL,
                                            date DATE NOT NULL,
			                    FOREIGN KEY (employee, task, date) REFERENCES hours(employee, task, date)
					    ON UPDATE RESTRICT ON DELETE RESTRICT,
                                            timestamp TIMESTAMP NOT NULL,
                                            INDEX(timestamp),
			                    duration DECIMAL (4,2) UNSIGNED NOT NULL,
			                    remote_address TEXT NOT NULL,
			                    remote_user INT UNSIGNED NOT NULL,
		                            reason TEXT CHARACTER SET utf8 NOT NULL) ENGINE=INNODB;

CREATE TABLE IF NOT EXISTS timecards (employee INT UNSIGNED NOT NULL,
                                      date DATE NOT NULL,
				      PRIMARY KEY (date, employee),
			              approved BOOLEAN NOT NULL) ENGINE=INNODB;
