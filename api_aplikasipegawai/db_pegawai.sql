-- phpMyAdmin SQL Dump
-- version 5.0.1
-- https://www.phpmyadmin.net/
--
-- Host: localhost
-- Generation Time: Jun 20, 2020 at 07:56 AM
-- Server version: 10.4.11-MariaDB
-- PHP Version: 7.4.2

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
SET AUTOCOMMIT = 0;
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `db_pegawai`
--

-- --------------------------------------------------------

--
-- Table structure for table `tb_pegawai`
--

CREATE TABLE `tb_pegawai` (
  `id` int(11) NOT NULL,
  `nama` varchar(30) DEFAULT NULL,
  `posisi` varchar(20) DEFAULT NULL,
  `gaji` int(11) DEFAULT NULL,
  `image` varchar(255) NOT NULL DEFAULT ''
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `tb_pegawai`
--

INSERT INTO `tb_pegawai` (`id`, `nama`, `posisi`, `gaji`, `image`) VALUES
(1, 'Muhammad Ridwan Ramadhan', 'Programmer', 2000, 'http://192.168.1.4/aplikasipegawai/uploads/1592540145931.jpeg'),
(2, 'Medi', 'Programmer', 1000, 'http://192.168.1.4/aplikasipegawai/uploads/1592555758211.jpeg'),
(3, 'Fina', 'Staff', 500, ''),
(8, 'ariq', 'perkapalan', 700, 'http://192.168.1.4/aplikasipegawai/uploads/1592555871087.jpeg'),
(9, 'aldo', 'komunikasi', 200, 'http://192.168.1.4/aplikasipegawai/uploads/1592557290484.jpeg'),
(10, 'Adit', 'Komunikasi', 2500, 'http://192.168.1.4/aplikasipegawai/uploads/1_l2AFc33U7grIeMML0a0unQ.jpeg'),
(11, 'Test', 'IT', 500, 'http://192.168.1.4/aplikasipegawai/uploads/1592631134004.jpeg'),
(13, 'harald', 'bank', 500, 'http://192.168.1.4/aplikasipegawai/uploads/1592632179022.jpeg');

--
-- Indexes for dumped tables
--

--
-- Indexes for table `tb_pegawai`
--
ALTER TABLE `tb_pegawai`
  ADD PRIMARY KEY (`id`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `tb_pegawai`
--
ALTER TABLE `tb_pegawai`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=14;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
