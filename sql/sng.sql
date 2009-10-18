-- MySQL dump 10.11
--
-- Host: localhost    Database: sarariman
-- ------------------------------------------------------
-- Server version	5.0.51a-24+lenny2-log

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `administrators`
--

DROP TABLE IF EXISTS `administrators`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `administrators` (
  `employee` int(10) unsigned NOT NULL
) ENGINE=MyISAM DEFAULT CHARSET=latin1;
SET character_set_client = @saved_cs_client;

--
-- Table structure for table `customers`
--

DROP TABLE IF EXISTS `customers`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `customers` (
  `id` int(10) unsigned NOT NULL auto_increment,
  `name` text character set utf8 NOT NULL,
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=12 DEFAULT CHARSET=latin1;
SET character_set_client = @saved_cs_client;

--
-- Table structure for table `hours`
--

DROP TABLE IF EXISTS `hours`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `hours` (
  `employee` int(10) unsigned NOT NULL,
  `task` int(10) unsigned NOT NULL,
  `date` date NOT NULL,
  `description` text character set utf8,
  `duration` decimal(4,2) unsigned NOT NULL,
  PRIMARY KEY  (`employee`,`task`,`date`),
  KEY `task` (`task`),
  CONSTRAINT `hours_ibfk_1` FOREIGN KEY (`task`) REFERENCES `tasks` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
SET character_set_client = @saved_cs_client;

--
-- Table structure for table `hours_changelog`
--

DROP TABLE IF EXISTS `hours_changelog`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `hours_changelog` (
  `employee` int(10) unsigned NOT NULL,
  `task` int(10) unsigned NOT NULL,
  `date` date NOT NULL,
  `timestamp` timestamp NOT NULL default CURRENT_TIMESTAMP on update CURRENT_TIMESTAMP,
  `duration` decimal(4,2) unsigned NOT NULL,
  `remote_address` text NOT NULL,
  `remote_user` int(10) unsigned NOT NULL,
  `reason` text character set utf8 NOT NULL,
  KEY `employee` (`employee`,`task`,`date`),
  KEY `timestamp` (`timestamp`),
  CONSTRAINT `hours_changelog_ibfk_1` FOREIGN KEY (`employee`, `task`, `date`) REFERENCES `hours` (`employee`, `task`, `date`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
SET character_set_client = @saved_cs_client;

--
-- Table structure for table `invoices`
--

DROP TABLE IF EXISTS `invoices`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `invoices` (
  `id` text NOT NULL,
  `employee` int(10) unsigned NOT NULL,
  `task` int(10) unsigned NOT NULL,
  `date` date NOT NULL,
  PRIMARY KEY  (`employee`,`task`,`date`),
  CONSTRAINT `invoices_ibfk_1` FOREIGN KEY (`employee`, `task`, `date`) REFERENCES `hours` (`employee`, `task`, `date`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
SET character_set_client = @saved_cs_client;

--
-- Table structure for table `projects`
--

DROP TABLE IF EXISTS `projects`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `projects` (
  `id` int(10) unsigned NOT NULL auto_increment,
  `customer` int(10) unsigned NOT NULL,
  `name` text character set utf8 NOT NULL,
  PRIMARY KEY  (`id`),
  KEY `customer` (`customer`),
  CONSTRAINT `projects_ibfk_1` FOREIGN KEY (`customer`) REFERENCES `customers` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=latin1;
SET character_set_client = @saved_cs_client;

--
-- Table structure for table `saic_tasks`
--

DROP TABLE IF EXISTS `saic_tasks`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `saic_tasks` (
  `task` int(10) unsigned NOT NULL,
  `po_line_item` int(10) unsigned NOT NULL,
  `charge_number` char(19) NOT NULL,
  `wbs` varchar(30) NOT NULL,
  KEY `task` (`task`),
  CONSTRAINT `saic_tasks_ibfk_1` FOREIGN KEY (`task`) REFERENCES `tasks` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
SET character_set_client = @saved_cs_client;

--
-- Table structure for table `tasks`
--

DROP TABLE IF EXISTS `tasks`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `tasks` (
  `id` int(10) unsigned NOT NULL auto_increment,
  `name` text character set utf8 NOT NULL,
  `billable` tinyint(1) NOT NULL,
  `description` text character set utf8,
  `active` tinyint(1) NOT NULL,
  `project` int(10) unsigned default NULL,
  PRIMARY KEY  (`id`),
  KEY `project` (`project`),
  CONSTRAINT `tasks_ibfk_1` FOREIGN KEY (`project`) REFERENCES `projects` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=5016 DEFAULT CHARSET=latin1;
SET character_set_client = @saved_cs_client;

--
-- Table structure for table `timecards`
--

DROP TABLE IF EXISTS `timecards`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `timecards` (
  `employee` int(10) unsigned NOT NULL,
  `date` date NOT NULL,
  `submitted_timestamp` timestamp NOT NULL default CURRENT_TIMESTAMP on update CURRENT_TIMESTAMP,
  `approved` tinyint(1) NOT NULL,
  PRIMARY KEY  (`date`,`employee`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
SET character_set_client = @saved_cs_client;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2009-10-18 20:36:07
