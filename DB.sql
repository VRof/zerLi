-- MySQL dump 10.13  Distrib 8.0.28, for Win64 (x86_64)
--
-- Host: localhost    Database: zerlidb
-- ------------------------------------------------------
-- Server version	8.0.28

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `balance`
--

DROP TABLE IF EXISTS `balance`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `balance` (
  `userid` int NOT NULL,
  `balance` double DEFAULT '0',
  PRIMARY KEY (`userid`)
) ENGINE=InnoDB DEFAULT CHARSET=ascii;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `balance`
--

LOCK TABLES `balance` WRITE;
/*!40000 ALTER TABLE `balance` DISABLE KEYS */;
INSERT INTO `balance` VALUES (1,0),(2,0),(3,0),(4,0);
/*!40000 ALTER TABLE `balance` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `cancellationrequests`
--

DROP TABLE IF EXISTS `cancellationrequests`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `cancellationrequests` (
  `orderID` int NOT NULL,
  `firstName` varchar(45) DEFAULT NULL,
  `lastName` varchar(45) DEFAULT NULL,
  `status` varchar(45) DEFAULT NULL,
  `price` double DEFAULT NULL,
  `requestDate` timestamp NULL DEFAULT NULL,
  `deliveryDate` timestamp NULL DEFAULT NULL,
  `shop` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`orderID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `cancellationrequests`
--

LOCK TABLES `cancellationrequests` WRITE;
/*!40000 ALTER TABLE `cancellationrequests` DISABLE KEYS */;
/*!40000 ALTER TABLE `cancellationrequests` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `catalog`
--

DROP TABLE IF EXISTS `catalog`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `catalog` (
  `name` varchar(100) NOT NULL,
  `id` int NOT NULL,
  `price` float DEFAULT NULL,
  `color` varchar(45) DEFAULT NULL,
  `imagePath` varchar(200) DEFAULT NULL,
  `isBundle` varchar(45) DEFAULT NULL,
  `discount` float DEFAULT '0',
  `priceorigin` float DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=ascii;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `catalog`
--

LOCK TABLES `catalog` WRITE;
/*!40000 ALTER TABLE `catalog` DISABLE KEYS */;
INSERT INTO `catalog` VALUES ('Colour burst',1,119.99,'pink','picturesCatalog/bundles/1.jpg','TRUE',0,0),('Blue moon (extra)',2,280,'blue','picturesCatalog/bundles/2.jpg','TRUE',0,0),('Amour',3,269.99,'purple','picturesCatalog/bundles/3.jpg','TRUE',0,0),('A new day',4,89,'white','picturesCatalog/bundles/4.jpg','TRUE',0,0),('Summer chill',5,65,'white','picturesCatalog/bundles/5.jpg','TRUE',0,0),('Dreamland',6,127,'pink','picturesCatalog/bundles/6.jpg','TRUE',0,0),('Sunrise',7,115,'pink','picturesCatalog/bundles/7.jpg','TRUE',0,0),('Blue moon',8,170,'purple','picturesCatalog/bundles/8.jpg','TRUE',0,0),('Red romance',9,189,'red','picturesCatalog/bundles/9.jpg','TRUE',0,0),('White snow',10,98,'white','picturesCatalog/bundles/10.jpg','TRUE',0,0),('Styilish spring',11,134.5,'pink','picturesCatalog/bundles/11.jpg','TRUE',0,0),('Peach perfection',12,145,'purple','picturesCatalog/bundles/12.jpg','TRUE',0,0),('Stardust',13,105,'white','picturesCatalog/bundles/13.jpg','TRUE',0,0),('Her day',14,129,'pink','picturesCatalog/bundles/14.jpg','TRUE',0,0),('Amaryllis',15,16,'red','picturesCatalog/items/Amaryllis.png','FALSE',0,0),('Aster',16,9.9,'pink','picturesCatalog/items/aster.png','FALSE',0,0),('Bluebell',17,19,'blue','picturesCatalog/items/bluebell.png','FALSE',0,0),('Cornflower',18,13,'blue','picturesCatalog/items/cornflower.png','FALSE',0,0),('Forget me not',19,10.59,'blue','picturesCatalog/items/forget me not.png','FALSE',0,0),('Foxglove',20,10,'pink','picturesCatalog/items/foxglove.png','FALSE',0,0),('Gerbera',21,8.9,'pink','picturesCatalog/items/Gerbera.png','FALSE',0,0),('Tulips',22,23.5,'red','picturesCatalog/items/tulips.png','FALSE',0,0),('Iris',25,13,'purple','picturesCatalog/items/iris.png','FALSE',0,0),('Jasmine',26,7.49,'white','picturesCatalog/items/jasmine.png','FALSE',0,0),('Lavender',27,14,'purple','picturesCatalog/items/lavender.png','FALSE',0,0),('Lily',28,16,'pink','picturesCatalog/items/Lily.png','FALSE',0,0),('Peony',29,8.9,'pink','picturesCatalog/items/peony.png','FALSE',0,0),('Rose',30,25,'red','picturesCatalog/items/rose.jpg','FALSE',0,0);
/*!40000 ALTER TABLE `catalog` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `complaints`
--

DROP TABLE IF EXISTS `complaints`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `complaints` (
  `complaintid` int NOT NULL AUTO_INCREMENT,
  `complaintText` varchar(1000) DEFAULT NULL,
  `complaintDone` varchar(25) DEFAULT 'no',
  `csid` int DEFAULT NULL,
  `shop` varchar(45) DEFAULT NULL,
  `date` timestamp(1) NULL DEFAULT NULL,
  PRIMARY KEY (`complaintid`)
) ENGINE=InnoDB AUTO_INCREMENT=34 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `complaints`
--

LOCK TABLES `complaints` WRITE;
/*!40000 ALTER TABLE `complaints` DISABLE KEYS */;
INSERT INTO `complaints` VALUES (19,'Client number 551, complaint about order number 5123 not delivered as asked! \nPlease take care of this complaint fast!!','no',3,'Prahim Special','2022-05-26 21:16:33.0'),(24,'Hello1','no',3,'Prahim Special','2022-05-29 07:56:52.2'),(25,'Hello world!','no',3,'Zer from yosi','2022-05-29 10:47:41.6'),(27,'abcd','no',16,'Zer from Yosi','2022-06-04 18:49:27.9'),(28,'abcd','no',16,'Zer from Yosi','2022-06-04 18:49:29.3'),(29,'abcd','no',16,'Zer from Yosi','2022-06-04 18:49:29.8'),(30,'abcd','no',16,'Zer from Yosi','2022-06-04 18:49:30.0'),(31,'abcd','no',16,'Prahim Special','2022-06-04 18:49:32.7'),(32,'abcd','no',16,'Prahim Special','2022-06-04 18:49:33.4'),(33,'abcd','no',16,'Prahim Special','2022-06-04 18:49:34.6');
/*!40000 ALTER TABLE `complaints` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `creditcards`
--

DROP TABLE IF EXISTS `creditcards`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `creditcards` (
  `userid` int NOT NULL,
  `creditCardNumber` varchar(45) DEFAULT NULL,
  `creditCardCVV` varchar(45) DEFAULT NULL,
  `creditCardExpiryDate` date DEFAULT NULL,
  PRIMARY KEY (`userid`)
) ENGINE=InnoDB DEFAULT CHARSET=ascii;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `creditcards`
--

LOCK TABLES `creditcards` WRITE;
/*!40000 ALTER TABLE `creditcards` DISABLE KEYS */;
INSERT INTO `creditcards` VALUES (1,'4917480231561200','664','2024-03-12'),(2,'4917480092831200','853','2025-07-14'),(3,'4917480235364200','123','2027-11-04'),(4,'4917480231234200','665','2029-09-08');
/*!40000 ALTER TABLE `creditcards` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `delivery`
--

DROP TABLE IF EXISTS `delivery`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `delivery` (
  `deliveryGuyID` int DEFAULT '14',
  `orderNumber` int NOT NULL,
  `price` double NOT NULL,
  `date` timestamp NOT NULL,
  `confirmed` varchar(45) DEFAULT 'no',
  `shop` varchar(200) NOT NULL,
  PRIMARY KEY (`orderNumber`)
) ENGINE=InnoDB DEFAULT CHARSET=ascii;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `delivery`
--

LOCK TABLES `delivery` WRITE;
/*!40000 ALTER TABLE `delivery` DISABLE KEYS */;
/*!40000 ALTER TABLE `delivery` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `login`
--

DROP TABLE IF EXISTS `login`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `login` (
  `userid` int NOT NULL,
  `username` varchar(100) DEFAULT NULL,
  `password` varchar(100) DEFAULT NULL,
  `usertype` varchar(45) DEFAULT NULL,
  `status` varchar(45) DEFAULT NULL,
  PRIMARY KEY (`userid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `login`
--

LOCK TABLES `login` WRITE;
/*!40000 ALTER TABLE `login` DISABLE KEYS */;
INSERT INTO `login` VALUES (1,'a','1','customer','active'),(2,'cu2','12345','customer','frozen'),(3,'cu3','12345','customer','active'),(4,'cu4','12345','customer','active'),(10,'sm3','12345','manager','active'),(11,'ceo','12345','ceo','active'),(12,'sm1','12345','manager','active'),(13,'sm2','12345','manager','active'),(14,'del1','12345','deliveryguy','active'),(15,'mw1','12345','marketingworker','active'),(16,'cs1','12345','customerservice','active');
/*!40000 ALTER TABLE `login` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `orders`
--

DROP TABLE IF EXISTS `orders`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `orders` (
  `orderNumber` int NOT NULL AUTO_INCREMENT,
  `price` double NOT NULL,
  `greetingCard` varchar(2000) DEFAULT NULL,
  `dOrder` varchar(8000) DEFAULT NULL,
  `shop` varchar(200) NOT NULL,
  `deliveryDate` timestamp NOT NULL,
  `orderDate` timestamp NOT NULL,
  `status` varchar(200) DEFAULT NULL,
  `confirmed` varchar(45) NOT NULL DEFAULT 'no',
  `confirmDate` timestamp NULL DEFAULT NULL,
  PRIMARY KEY (`orderNumber`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `orders`
--

LOCK TABLES `orders` WRITE;
/*!40000 ALTER TABLE `orders` DISABLE KEYS */;
INSERT INTO `orders` VALUES (1,2189.92,'','Amour x10 price: 2699.9 NIS\n-----------------------------\nAfter 20% first order discount:\nOrder price: 2159.92 NIS\n\nDelivery method: delivery(30 NIS)\nFull name: a\nDelivery address: aa\nPhone: 1234567890\nDelivery date: 2022-06-25 06:40\nShop: My dear flower\n-----------------------------\nPrice include delivery: 2189.92 NIS\n\nPayment method:\nName: Habib Ibrahim\nCredit card: ************1200\n---------------------------\nFinal price: 2189.92 NIS\n','My dear flower','2022-06-25 03:40:00','2022-06-06 18:56:26','delivered','yes','2022-06-06 18:56:48'),(2,461.98,'hello worl','Amour x2 price: 539.98 NIS\n-----------------------------\nAfter 20% first order discount:\nOrder price: 431.98 NIS\n\nDelivery method: delivery(30 NIS)\nFull name: a\nDelivery address: Hashoshanim 15\nPhone: 78878788888\nDelivery date: 2022-06-15 07:15\nShop: My dear flower\nBlessing message: hello worl\n-----------------------------\nPrice include delivery: 461.98 NIS\n\nPayment method:\nName: Vitaly  Rofman\nCredit card: ************4200\n---------------------------\nFinal price: 461.98 NIS\n','My dear flower','2022-06-15 04:15:00','2022-06-06 19:32:55','delivered','yes','2022-06-06 19:33:56');
/*!40000 ALTER TABLE `orders` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `registration`
--

DROP TABLE IF EXISTS `registration`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `registration` (
  `userid` int NOT NULL,
  `firstname` varchar(45) DEFAULT NULL,
  `lastname` varchar(45) DEFAULT NULL,
  `creditCardNumber` varchar(45) DEFAULT NULL,
  `telephoneNumber` varchar(45) DEFAULT NULL,
  `email` varchar(45) DEFAULT NULL,
  `type` varchar(45) DEFAULT NULL,
  `creditCardCVV` varchar(45) DEFAULT NULL,
  `creditCardExpiryDate` varchar(45) DEFAULT NULL,
  PRIMARY KEY (`userid`)
) ENGINE=InnoDB DEFAULT CHARSET=ascii;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `registration`
--

LOCK TABLES `registration` WRITE;
/*!40000 ALTER TABLE `registration` DISABLE KEYS */;
INSERT INTO `registration` VALUES (1,'Habib','Ibrahim','4917480231561200','0512123434','bb@ZerLi.com','customer','664','2024-03-12');
/*!40000 ALTER TABLE `registration` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `reports`
--

DROP TABLE IF EXISTS `reports`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `reports` (
  `reportid` int NOT NULL AUTO_INCREMENT,
  `type` varchar(200) DEFAULT NULL,
  `month` varchar(200) DEFAULT NULL,
  `shop` varchar(200) DEFAULT NULL,
  `reportdetails` varchar(8000) DEFAULT NULL,
  PRIMARY KEY (`reportid`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `reports`
--

LOCK TABLES `reports` WRITE;
/*!40000 ALTER TABLE `reports` DISABLE KEYS */;
INSERT INTO `reports` VALUES (1,'order report','2022/5/1','My dear flower','\norder number: 1\nprice: 1905.98\norder details:Summer chill x2 price: 130.0₪\nRed romance x2 price: 378.0₪\nCustom: jub1 ,list of items:\n	Blue moon (extra) x2 price: 560.0₪\n	Amour x2 price: 539.98₪\n	Foxglove x2 price: 20.0₪\nCustom: jub2 ,list of items:\n	Stardust x2 price: 210.0₪\n	Her day x3 price: 387.0₪\n	Foxglove x12 price: 120.0₪\n-----------------------------\nAfter 20% first order discount:\nOrder price: 1875.98₪\n\nDelivery method: delivery(30₪)\nFull name: jub jubov\nDelivery address: jub str\nPhone: 0547832322\nDelivery date: 2022-06-17 03:15\nShop: My dear flower\n-----------------------------\nPrice include delivery: 1905.98₪\n\nPayment method:\nName: Habib Ibrahim\nCredit card: ************1200\n---------------------------\nFinal price: 1905.98₪\n\ndelivery date: 2022-06-17'),(2,'income report','2022/5/1','My dear flower','Overall income:0.0'),(3,'order report','2022/5/1','Prahim Special',''),(4,'income report','2022/5/1','Prahim Special','Overall income:0.0'),(5,'order report','2022/5/1','Zer from Yosi',''),(6,'income report','2022/5/1','Zer from Yosi','Overall income:0.0');
/*!40000 ALTER TABLE `reports` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `shopmanager`
--

DROP TABLE IF EXISTS `shopmanager`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `shopmanager` (
  `userid` int NOT NULL,
  `shop` varchar(200) NOT NULL,
  PRIMARY KEY (`userid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `shopmanager`
--

LOCK TABLES `shopmanager` WRITE;
/*!40000 ALTER TABLE `shopmanager` DISABLE KEYS */;
INSERT INTO `shopmanager` VALUES (10,'My dear flower'),(12,'Prahim Special'),(13,'Zer from Yosi');
/*!40000 ALTER TABLE `shopmanager` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `survey`
--

DROP TABLE IF EXISTS `survey`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `survey` (
  `idsurvey` int NOT NULL,
  `q1` varchar(200) DEFAULT NULL,
  `q2` varchar(200) DEFAULT NULL,
  `q3` varchar(200) DEFAULT NULL,
  `q4` varchar(200) DEFAULT NULL,
  `q5` varchar(200) DEFAULT NULL,
  `q6` varchar(200) DEFAULT NULL,
  `a1` int DEFAULT NULL,
  `a2` int DEFAULT NULL,
  `a3` int DEFAULT NULL,
  `a4` int DEFAULT NULL,
  `a5` int DEFAULT NULL,
  `a6` int DEFAULT NULL,
  PRIMARY KEY (`idsurvey`)
) ENGINE=InnoDB DEFAULT CHARSET=ascii;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `survey`
--

LOCK TABLES `survey` WRITE;
/*!40000 ALTER TABLE `survey` DISABLE KEYS */;
INSERT INTO `survey` VALUES (1,'How would you rate the support you received?','How happy are you with your product','How likely are you to recommend us to a friend or colleague','How easy did we make it to solve your problem?','How would you rate the buying experience?','How likely are you to return to our website?',3,4,2,5,4,5),(2,'How would you rate the support you received?','How happy are you with your product','How likely are you to recommend us to a friend or colleague','How easy did we make it to solve your problem?','How would you rate the buying experience?','How likely are you to return to our website?',5,3,3,2,1,4);
/*!40000 ALTER TABLE `survey` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `surveyanswers`
--

DROP TABLE IF EXISTS `surveyanswers`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `surveyanswers` (
  `answernum` int NOT NULL AUTO_INCREMENT,
  `idsurvey` int DEFAULT NULL,
  `a1` int DEFAULT NULL,
  `a2` int DEFAULT NULL,
  `a3` int DEFAULT NULL,
  `a4` int DEFAULT NULL,
  `a5` int DEFAULT NULL,
  `a6` int DEFAULT NULL,
  `surveydate` timestamp(6) NULL DEFAULT NULL,
  PRIMARY KEY (`answernum`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `surveyanswers`
--

LOCK TABLES `surveyanswers` WRITE;
/*!40000 ALTER TABLE `surveyanswers` DISABLE KEYS */;
/*!40000 ALTER TABLE `surveyanswers` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `surveyinfo`
--

DROP TABLE IF EXISTS `surveyinfo`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `surveyinfo` (
  `surveyid` int NOT NULL,
  `q1` varchar(200) NOT NULL DEFAULT 'q1',
  `q2` varchar(200) NOT NULL DEFAULT 'q2',
  `q3` varchar(200) NOT NULL DEFAULT 'q3',
  `q4` varchar(200) NOT NULL DEFAULT 'q4',
  `q5` varchar(200) NOT NULL DEFAULT 'q5',
  `q6` varchar(200) NOT NULL DEFAULT 'q6',
  PRIMARY KEY (`surveyid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `surveyinfo`
--

LOCK TABLES `surveyinfo` WRITE;
/*!40000 ALTER TABLE `surveyinfo` DISABLE KEYS */;
/*!40000 ALTER TABLE `surveyinfo` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `surveyresults`
--

DROP TABLE IF EXISTS `surveyresults`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `surveyresults` (
  `surveyid` int NOT NULL,
  `surveyinfo` varchar(45) DEFAULT NULL,
  `link` varchar(45) DEFAULT NULL,
  `csid` int DEFAULT NULL,
  PRIMARY KEY (`surveyid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `surveyresults`
--

LOCK TABLES `surveyresults` WRITE;
/*!40000 ALTER TABLE `surveyresults` DISABLE KEYS */;
INSERT INTO `surveyresults` VALUES (1,'20/09/2020 survey for shop number 5',' external/Surveyresults1.pdf',16);
/*!40000 ALTER TABLE `surveyresults` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `userorders`
--

DROP TABLE IF EXISTS `userorders`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `userorders` (
  `userid` int DEFAULT NULL,
  `orderid` int NOT NULL AUTO_INCREMENT,
  PRIMARY KEY (`orderid`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `userorders`
--

LOCK TABLES `userorders` WRITE;
/*!40000 ALTER TABLE `userorders` DISABLE KEYS */;
INSERT INTO `userorders` VALUES (1,1),(3,2);
/*!40000 ALTER TABLE `userorders` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `users`
--

DROP TABLE IF EXISTS `users`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `users` (
  `userid` int NOT NULL,
  `firstname` varchar(45) NOT NULL,
  `lastname` varchar(45) NOT NULL,
  `email` varchar(45) NOT NULL,
  `phonenumber` varchar(45) NOT NULL,
  PRIMARY KEY (`userid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `users`
--

LOCK TABLES `users` WRITE;
/*!40000 ALTER TABLE `users` DISABLE KEYS */;
INSERT INTO `users` VALUES (1,'Habib','Ibrahim','bb@ZerLi.com','0512123434'),(2,'Ibrahim','Daud','cc@ZerLi.com','0587654321'),(3,'Vitaly ','Rofman','aa@ZerLi.com','0512345678'),(4,'Yosif','Hosen','yy@ZerLi.com','0598812500'),(10,'Leonardo','Dicaprio','leo@Zerli.com','0536749875'),(11,'Lex','Luthor','jub@Zerli.com','0534234115'),(12,'Dexter','Morgan','tr@Zerli.com','0543846138'),(13,'Alex','Grey','alex@Zerli.com','0547891345'),(14,'Maria','Cliffman','mia@Zerli.com','0547289632'),(15,'Jonathan','Radcliffe','sins@Zerli.com','0548933264'),(16,'Steve','Harvey','steve@Zerli.com','0546782234');
/*!40000 ALTER TABLE `users` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2022-06-06 22:36:53
