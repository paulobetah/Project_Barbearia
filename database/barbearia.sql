CREATE DATABASE  IF NOT EXISTS `barbearia` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci */ /*!80016 DEFAULT ENCRYPTION='N' */;
USE `barbearia`;
-- MySQL dump 10.13  Distrib 8.0.46, for Win64 (x86_64)
--
-- Host: localhost    Database: barbearia
-- ------------------------------------------------------
-- Server version	8.0.43

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
-- Table structure for table `agendamento_servicos`
--

DROP TABLE IF EXISTS `agendamento_servicos`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `agendamento_servicos` (
  `agendamento_id` int NOT NULL,
  `servico_id` int NOT NULL,
  PRIMARY KEY (`agendamento_id`,`servico_id`),
  KEY `servico_id` (`servico_id`),
  CONSTRAINT `agendamento_servicos_ibfk_1` FOREIGN KEY (`agendamento_id`) REFERENCES `agendamentos` (`id`),
  CONSTRAINT `agendamento_servicos_ibfk_2` FOREIGN KEY (`servico_id`) REFERENCES `servicos` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `agendamento_servicos`
--

LOCK TABLES `agendamento_servicos` WRITE;
/*!40000 ALTER TABLE `agendamento_servicos` DISABLE KEYS */;
/*!40000 ALTER TABLE `agendamento_servicos` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `agendamentos`
--

DROP TABLE IF EXISTS `agendamentos`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `agendamentos` (
  `id` int NOT NULL AUTO_INCREMENT,
  `cliente_id` int NOT NULL,
  `barbeiro_id` int NOT NULL,
  `data_hora` datetime NOT NULL,
  `status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT 'Agendado',
  `criado_em` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_agendamento_barbeiro_horario` (`barbeiro_id`,`data_hora`),
  KEY `cliente_id` (`cliente_id`),
  KEY `barbeiro_id` (`barbeiro_id`),
  CONSTRAINT `agendamentos_ibfk_1` FOREIGN KEY (`cliente_id`) REFERENCES `clientes` (`id`),
  CONSTRAINT `agendamentos_ibfk_2` FOREIGN KEY (`barbeiro_id`) REFERENCES `barbeiros` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `agendamentos`
--

LOCK TABLES `agendamentos` WRITE;
/*!40000 ALTER TABLE `agendamentos` DISABLE KEYS */;
/*!40000 ALTER TABLE `agendamentos` ENABLE KEYS */;
UNLOCK TABLES;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
/*!50003 CREATE*/ /*!50017 DEFINER=`root`@`localhost`*/ /*!50003 TRIGGER `trg_log_novo_agendamento` AFTER INSERT ON `agendamentos` FOR EACH ROW BEGIN
    INSERT INTO log_agendamentos (
        agendamento_id,
        acao,
        data_log,
        cliente_id,
        barbeiro_id
    ) VALUES (
        NEW.id,
        'Novo agendamento criado',
        NOW(),
        NEW.cliente_id,
        NEW.barbeiro_id
    );
END */;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;

--
-- Table structure for table `barbeiro_escalas`
--

DROP TABLE IF EXISTS `barbeiro_escalas`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `barbeiro_escalas` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `barbeiro_id` int NOT NULL,
  `dia_semana` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `hora_inicio` time NOT NULL,
  `hora_fim` time NOT NULL,
  PRIMARY KEY (`id`),
  KEY `barbeiro_id` (`barbeiro_id`),
  CONSTRAINT `barbeiro_escalas_ibfk_1` FOREIGN KEY (`barbeiro_id`) REFERENCES `barbeiros` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=13 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `barbeiro_escalas`
--

LOCK TABLES `barbeiro_escalas` WRITE;
/*!40000 ALTER TABLE `barbeiro_escalas` DISABLE KEYS */;
INSERT INTO `barbeiro_escalas` VALUES (1,1,'Segunda','08:00:00','18:00:00'),(2,1,'Terça','08:00:00','18:00:00'),(3,1,'Quarta','08:00:00','18:00:00'),(4,2,'Segunda','08:00:00','18:00:00'),(5,2,'Quinta','08:00:00','18:00:00'),(6,2,'Sexta','08:00:00','18:00:00'),(7,1,'SEGUNDA','09:00:00','19:00:00'),(8,1,'TERCA','09:00:00','20:00:00'),(9,1,'QUARTA','09:00:00','21:00:00'),(10,1,'QUINTA','09:00:00','21:00:00'),(11,1,'SEXTA','09:00:00','21:00:00'),(12,1,'SABADO','09:00:00','20:00:00');
/*!40000 ALTER TABLE `barbeiro_escalas` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `barbeiros`
--

DROP TABLE IF EXISTS `barbeiros`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `barbeiros` (
  `id` int NOT NULL AUTO_INCREMENT,
  `nome` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `especialidade` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL,
  `foto` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `barbeiros`
--

LOCK TABLES `barbeiros` WRITE;
/*!40000 ALTER TABLE `barbeiros` DISABLE KEYS */;
INSERT INTO `barbeiros` VALUES (1,'Jhon Fonseca','cORTES REAL','image/barbeiro jhon.jpeg'),(2,'Radames','Rei dos cortes','image/barbeiro radames.jpeg');
/*!40000 ALTER TABLE `barbeiros` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `clientes`
--

DROP TABLE IF EXISTS `clientes`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `clientes` (
  `id` int NOT NULL AUTO_INCREMENT,
  `nome` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `email` varchar(150) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `telefone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL,
  `senha` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `ativo` bit(1) DEFAULT NULL,
  `criado_em` datetime(6) DEFAULT NULL,
  `papel` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `email` (`email`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `clientes`
--

LOCK TABLES `clientes` WRITE;
/*!40000 ALTER TABLE `clientes` DISABLE KEYS */;
INSERT INTO `clientes` VALUES (1,'Paulo Rogério SilvaCordeiro','paulorogeriosilva026@gmail.com','98970228034','$2a$10$hWTe/JauO/BFYh3tno0qKeW0RgI9CiIppMwQ.5pOHRunga6CIdGFm',_binary '','2026-09-15 15:09:00.616428','ADMIN');
/*!40000 ALTER TABLE `clientes` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `log_agendamentos`
--

DROP TABLE IF EXISTS `log_agendamentos`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `log_agendamentos` (
  `id` int NOT NULL AUTO_INCREMENT,
  `agendamento_id` int DEFAULT NULL,
  `acao` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL,
  `data_log` datetime DEFAULT NULL,
  `cliente_id` int DEFAULT NULL,
  `barbeiro_id` int DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `agendamento_id` (`agendamento_id`),
  CONSTRAINT `log_agendamentos_ibfk_1` FOREIGN KEY (`agendamento_id`) REFERENCES `agendamentos` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=13 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `log_agendamentos`
--

LOCK TABLES `log_agendamentos` WRITE;
/*!40000 ALTER TABLE `log_agendamentos` DISABLE KEYS */;
INSERT INTO `log_agendamentos` VALUES (1,1,'Novo agendamento criado','2026-04-21 20:03:00',2,1),(2,2,'Novo agendamento criado','2026-04-21 20:32:05',2,2),(3,3,'Novo agendamento criado','2026-04-21 21:13:27',2,1),(4,4,'Novo agendamento criado','2026-04-22 00:08:32',3,1),(5,5,'Novo agendamento criado','2026-04-22 14:01:56',3,2),(6,6,'Novo agendamento criado','2026-04-23 19:42:35',3,1),(7,7,'Novo agendamento criado','2026-04-23 19:59:38',3,1),(8,8,'Novo agendamento criado','2026-06-10 22:43:17',3,1),(9,9,'Novo agendamento criado','2026-06-11 18:51:47',3,1),(10,10,'Novo agendamento criado','2026-06-11 19:02:45',5,2),(11,11,'Novo agendamento criado','2026-06-11 19:03:17',5,2),(12,13,'Novo agendamento criado','2026-06-18 21:03:36',5,1);
/*!40000 ALTER TABLE `log_agendamentos` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `servicos`
--

DROP TABLE IF EXISTS `servicos`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `servicos` (
  `id` int NOT NULL AUTO_INCREMENT,
  `nome` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `preco` decimal(38,2) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `servicos`
--

LOCK TABLES `servicos` WRITE;
/*!40000 ALTER TABLE `servicos` DISABLE KEYS */;
INSERT INTO `servicos` VALUES (1,'Corte na Tesoura',45.00),(2,'Sobrancelha',10.00),(3,'Corte e barba xpress',55.00),(4,'Corte e barboterapia',75.00),(5,'Escova',25.00),(6,'Luzes',150.00);
/*!40000 ALTER TABLE `servicos` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping events for database 'barbearia'
--

--
-- Dumping routines for database 'barbearia'
--
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-09-21 12:50:20
