-- MySQL dump 10.11
--
-- Host: localhost    Database: sarariman
-- ------------------------------------------------------
-- Server version	5.0.51a-24+lenny5-log

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

SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `administrators` (
  `employee` int(10) unsigned NOT NULL COMMENT 'LDAP uid of the employee.',
  PRIMARY KEY  (`employee`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1 COMMENT='Employees who have permission to modify the database.';
SET character_set_client = @saved_cs_client;

--
-- Table structure for table `approvers`
--

SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `approvers` (
  `employee` int(10) unsigned NOT NULL,
  PRIMARY KEY  (`employee`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;
SET character_set_client = @saved_cs_client;

--
-- Table structure for table `billed_services`
--

SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `billed_services` (
  `id` int(10) unsigned NOT NULL auto_increment,
  `service_agreement` int(10) unsigned NOT NULL,
  `pop_start` date NOT NULL,
  `pop_end` date NOT NULL,
  `invoice` int(10) unsigned default NULL,
  PRIMARY KEY  (`id`),
  KEY `service_agreement` (`service_agreement`),
  KEY `invoice` (`invoice`)
) ENGINE=MyISAM AUTO_INCREMENT=40 DEFAULT CHARSET=latin1;
SET character_set_client = @saved_cs_client;

--
-- Table structure for table `contacts`
--

SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `contacts` (
  `id` int(10) unsigned NOT NULL auto_increment,
  `name` text,
  `title` text,
  `email` varchar(50) default NULL,
  `phone` varchar(20) default NULL,
  `fax` varchar(20) default NULL,
  `mobile` varchar(20) default NULL,
  `street` text,
  `city` text,
  `state` varchar(2) default NULL,
  `zip` varchar(10) default NULL,
  PRIMARY KEY  (`id`)
) ENGINE=MyISAM AUTO_INCREMENT=44 DEFAULT CHARSET=latin1;
SET character_set_client = @saved_cs_client;

--
-- Table structure for table `customers`
--

SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `customers` (
  `id` int(10) unsigned NOT NULL auto_increment,
  `name` text character set utf8 NOT NULL,
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=26 DEFAULT CHARSET=latin1;
SET character_set_client = @saved_cs_client;

--
-- Table structure for table `direct_rate`
--

SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `direct_rate` (
  `employee` int(10) unsigned default NULL,
  `effective` date default NULL,
  `rate` decimal(6,2) default NULL
) ENGINE=MyISAM DEFAULT CHARSET=latin1;
SET character_set_client = @saved_cs_client;

--
-- Table structure for table `expenses`
--

SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `expenses` (
  `id` int(10) unsigned NOT NULL auto_increment,
  `employee` int(10) unsigned default NULL,
  `task` int(10) unsigned default NULL,
  `date` date default NULL,
  `description` text,
  `cost` decimal(8,2) default NULL,
  `invoice` int(10) unsigned default NULL,
  PRIMARY KEY  (`id`),
  KEY `task` (`task`),
  KEY `invoice` (`invoice`)
) ENGINE=MyISAM AUTO_INCREMENT=63 DEFAULT CHARSET=latin1;
SET character_set_client = @saved_cs_client;

--
-- Table structure for table `hours`
--

SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `hours` (
  `employee` int(10) unsigned NOT NULL,
  `task` int(10) unsigned NOT NULL,
  `date` date NOT NULL,
  `description` text character set utf8,
  `duration` decimal(4,2) unsigned NOT NULL,
  `service_agreement` int(10) unsigned default NULL,
  PRIMARY KEY  (`employee`,`task`,`date`),
  KEY `task` (`task`),
  CONSTRAINT `hours_ibfk_1` FOREIGN KEY (`task`) REFERENCES `tasks` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
SET character_set_client = @saved_cs_client;

--
-- Table structure for table `hours_changelog`
--

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
  KEY `timestamp` (`timestamp`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
SET character_set_client = @saved_cs_client;

--
-- Table structure for table `invoice_email_log`
--

SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `invoice_email_log` (
  `invoice` int(10) unsigned NOT NULL,
  `sender` int(10) unsigned NOT NULL,
  `sent` timestamp NOT NULL default CURRENT_TIMESTAMP,
  KEY `invoice` (`invoice`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;
SET character_set_client = @saved_cs_client;

--
-- Table structure for table `invoice_info`
--

SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `invoice_info` (
  `id` int(10) unsigned NOT NULL auto_increment,
  `sent` date NOT NULL,
  `project` int(10) unsigned default NULL,
  `description` text,
  `payment_received` date default NULL,
  `comments` text,
  `customer` int(10) unsigned NOT NULL,
  `pop_start` date NOT NULL,
  `pop_end` date NOT NULL,
  PRIMARY KEY  (`id`),
  KEY `project` (`project`),
  KEY `customer` (`customer`)
) ENGINE=MyISAM AUTO_INCREMENT=831 DEFAULT CHARSET=latin1;
SET character_set_client = @saved_cs_client;

--
-- Table structure for table `invoice_managers`
--

SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `invoice_managers` (
  `employee` int(10) unsigned NOT NULL,
  PRIMARY KEY  (`employee`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;
SET character_set_client = @saved_cs_client;

--
-- Table structure for table `invoices`
--

SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `invoices` (
  `id` varchar(12) NOT NULL,
  `employee` int(10) unsigned NOT NULL,
  `task` int(10) unsigned NOT NULL,
  `date` date NOT NULL,
  PRIMARY KEY  (`employee`,`task`,`date`),
  CONSTRAINT `invoices_ibfk_1` FOREIGN KEY (`employee`, `task`, `date`) REFERENCES `hours` (`employee`, `task`, `date`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
SET character_set_client = @saved_cs_client;

--
-- Table structure for table `labor_categories`
--

SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `labor_categories` (
  `id` int(10) unsigned NOT NULL auto_increment,
  `project` int(10) unsigned NOT NULL,
  `rate` decimal(6,2) NOT NULL,
  `pop_start` date NOT NULL,
  `pop_end` date NOT NULL,
  `name` varchar(50) default NULL,
  PRIMARY KEY  (`id`),
  KEY `project` (`project`)
) ENGINE=MyISAM AUTO_INCREMENT=63 DEFAULT CHARSET=latin1;
SET character_set_client = @saved_cs_client;

--
-- Table structure for table `labor_category_assignments`
--

SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `labor_category_assignments` (
  `employee` int(10) unsigned NOT NULL,
  `pop_start` date NOT NULL,
  `pop_end` date NOT NULL,
  `labor_category` int(10) unsigned NOT NULL,
  KEY `labor_category` (`labor_category`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;
SET character_set_client = @saved_cs_client;

--
-- Table structure for table `line_items`
--

SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `line_items` (
  `id` int(10) unsigned NOT NULL,
  `project` int(10) unsigned NOT NULL,
  `funded` decimal(12,2) default NULL,
  `pop_start` date default NULL,
  `pop_end` date default NULL,
  `description` text,
  KEY `project` (`project`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;
SET character_set_client = @saved_cs_client;

--
-- Table structure for table `project_invoice_contacts`
--

SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `project_invoice_contacts` (
  `project` int(10) unsigned NOT NULL,
  `contact` int(10) unsigned NOT NULL,
  KEY `project` (`project`),
  KEY `contact` (`contact`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;
SET character_set_client = @saved_cs_client;

--
-- Table structure for table `project_managers`
--

SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `project_managers` (
  `employee` int(10) unsigned NOT NULL,
  `project` int(10) unsigned NOT NULL,
  PRIMARY KEY  (`employee`,`project`),
  KEY `project` (`project`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;
SET character_set_client = @saved_cs_client;

--
-- Table structure for table `project_timesheet_contacts`
--

SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `project_timesheet_contacts` (
  `project` int(10) unsigned NOT NULL,
  `contact` int(10) unsigned NOT NULL,
  KEY `project` (`project`),
  KEY `contact` (`contact`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;
SET character_set_client = @saved_cs_client;

--
-- Table structure for table `project_timesheet_email_log`
--

SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `project_timesheet_email_log` (
  `project` int(10) unsigned NOT NULL,
  `sender` int(10) unsigned NOT NULL,
  `week` date NOT NULL,
  `sent` timestamp NOT NULL default CURRENT_TIMESTAMP,
  KEY `project` (`project`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;
SET character_set_client = @saved_cs_client;

--
-- Table structure for table `projects`
--

SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `projects` (
  `id` int(10) unsigned NOT NULL auto_increment,
  `customer` int(10) unsigned NOT NULL,
  `name` text character set utf8 NOT NULL,
  `funded` decimal(12,2) default NULL,
  `contract_number` text,
  `subcontract_number` text,
  `pop_start` date NOT NULL,
  `pop_end` date NOT NULL,
  `terms` int(10) unsigned NOT NULL,
  `previously_billed` decimal(12,2) default NULL,
  `odc_fee` decimal(5,5) default NULL,
  `active` tinyint(1) NOT NULL,
  PRIMARY KEY  (`id`),
  KEY `customer` (`customer`),
  CONSTRAINT `projects_ibfk_1` FOREIGN KEY (`customer`) REFERENCES `customers` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=35 DEFAULT CHARSET=latin1;
SET character_set_client = @saved_cs_client;

--
-- Table structure for table `saic_tasks`
--

SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `saic_tasks` (
  `task` int(10) unsigned NOT NULL,
  `po_line_item` int(10) unsigned NOT NULL,
  `charge_number` varchar(30) default NULL,
  `wbs` varchar(30) NOT NULL,
  KEY `task` (`task`),
  CONSTRAINT `saic_tasks_ibfk_1` FOREIGN KEY (`task`) REFERENCES `tasks` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
SET character_set_client = @saved_cs_client;

--
-- Table structure for table `service_agreements`
--

SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `service_agreements` (
  `id` int(10) unsigned NOT NULL auto_increment,
  `project` int(10) unsigned NOT NULL,
  `pop_start` date NOT NULL,
  `pop_end` date NOT NULL,
  `billing_period` varchar(30) NOT NULL,
  `period_rate` decimal(8,2) NOT NULL,
  `description` text NOT NULL,
  PRIMARY KEY  (`id`),
  KEY `project` (`project`)
) ENGINE=MyISAM AUTO_INCREMENT=8 DEFAULT CHARSET=latin1;
SET character_set_client = @saved_cs_client;

--
-- Table structure for table `task_assignments`
--

SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `task_assignments` (
  `employee` int(10) unsigned NOT NULL,
  `task` int(10) unsigned NOT NULL,
  PRIMARY KEY  (`employee`,`task`),
  KEY `task` (`task`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;
SET character_set_client = @saved_cs_client;

--
-- Table structure for table `task_grouping`
--

SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `task_grouping` (
  `id` int(10) unsigned NOT NULL auto_increment,
  `pop_start` date default NULL,
  `pop_end` date default NULL,
  `name` text NOT NULL,
  PRIMARY KEY  (`id`)
) ENGINE=MyISAM AUTO_INCREMENT=4 DEFAULT CHARSET=latin1;
SET character_set_client = @saved_cs_client;

--
-- Table structure for table `task_grouping_element`
--

SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `task_grouping_element` (
  `grouping` int(10) unsigned NOT NULL,
  `fraction` decimal(2,2) default NULL,
  `task` int(10) unsigned NOT NULL,
  KEY `grouping` (`grouping`),
  KEY `task` (`task`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;
SET character_set_client = @saved_cs_client;

--
-- Table structure for table `task_grouping_employee`
--

SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `task_grouping_employee` (
  `grouping` int(10) unsigned NOT NULL,
  `employee` int(10) unsigned NOT NULL,
  KEY `grouping` (`grouping`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;
SET character_set_client = @saved_cs_client;

--
-- Table structure for table `tasks`
--

SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `tasks` (
  `id` int(10) unsigned NOT NULL auto_increment,
  `name` text character set utf8 NOT NULL,
  `billable` tinyint(1) NOT NULL,
  `description` text character set utf8,
  `active` tinyint(1) NOT NULL,
  `project` int(10) unsigned default NULL,
  `line_item` int(10) unsigned default NULL,
  PRIMARY KEY  (`id`),
  KEY `project` (`project`),
  CONSTRAINT `tasks_ibfk_1` FOREIGN KEY (`project`) REFERENCES `projects` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=5130 DEFAULT CHARSET=latin1;
SET character_set_client = @saved_cs_client;

--
-- Table structure for table `timecards`
--

SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `timecards` (
  `employee` int(10) unsigned NOT NULL,
  `date` date NOT NULL,
  `approved` tinyint(1) NOT NULL,
  `submitted_timestamp` timestamp NOT NULL default CURRENT_TIMESTAMP,
  `approved_timestamp` timestamp NOT NULL default '0000-00-00 00:00:00',
  `approver` int(11) default NULL,
  PRIMARY KEY  (`date`,`employee`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
SET character_set_client = @saved_cs_client;

--
-- Table structure for table `timesheet_managers`
--

SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `timesheet_managers` (
  `employee` int(10) unsigned NOT NULL,
  PRIMARY KEY  (`employee`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;
SET character_set_client = @saved_cs_client;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2011-05-20 13:52:01
