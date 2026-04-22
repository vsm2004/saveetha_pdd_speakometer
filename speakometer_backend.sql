-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Generation Time: Apr 07, 2026 at 06:52 AM
-- Server version: 10.4.32-MariaDB
-- PHP Version: 8.0.30

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `speakometer_backend`
--

-- --------------------------------------------------------

--
-- Table structure for table `feedback`
--

CREATE TABLE `feedback` (
  `id` int(11) NOT NULL,
  `user_id` int(11) NOT NULL,
  `message` text NOT NULL,
  `category` varchar(50) DEFAULT NULL,
  `priority` varchar(20) DEFAULT NULL,
  `sentiment` varchar(20) DEFAULT NULL,
  `rating` int(11) DEFAULT 5,
  `created_at` timestamp NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `feedback`
--

INSERT INTO `feedback` (`id`, `user_id`, `message`, `category`, `priority`, `sentiment`, `rating`, `created_at`) VALUES
(1, 1, 'The speech analysis is very accurate, but I\'d love more accent options!', NULL, NULL, NULL, 5, '2026-02-28 18:26:57'),
(2, 1, 'The transcription is wrong.', 'AI Accuracy', 'Medium', 'Positive/Neutral', 2, '2026-03-02 04:58:16');

-- --------------------------------------------------------

--
-- Table structure for table `sessions`
--

CREATE TABLE `sessions` (
  `id` int(11) NOT NULL,
  `user_id` int(11) NOT NULL,
  `score` int(11) DEFAULT NULL,
  `fillers_count` int(11) DEFAULT 0,
  `stretching_level` varchar(50) DEFAULT 'none',
  `confidence` int(11) DEFAULT 0,
  `created_at` timestamp NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `sessions`
--

INSERT INTO `sessions` (`id`, `user_id`, `score`, `fillers_count`, `stretching_level`, `confidence`, `created_at`) VALUES
(1, 1, 85, 3, 'medium', 92, '2026-02-28 17:28:55'),
(2, 6, 100, 0, 'none', 100, '2026-03-19 15:22:08'),
(3, 6, 100, 0, 'high', 100, '2026-03-24 02:40:41'),
(4, 9, 100, 0, 'high', 100, '2026-03-27 23:22:11'),
(5, 9, 99, 0, 'high', 100, '2026-03-27 23:54:26'),
(6, 9, 99, 0, 'none', 100, '2026-03-28 01:09:51');

-- --------------------------------------------------------

--
-- Table structure for table `users`
--

CREATE TABLE `users` (
  `id` int(11) NOT NULL,
  `name` varchar(100) DEFAULT NULL,
  `email` varchar(150) NOT NULL,
  `password` varchar(255) NOT NULL,
  `created_at` timestamp NOT NULL DEFAULT current_timestamp(),
  `premium_status` tinyint(1) DEFAULT 0,
  `premium_expiry` date DEFAULT NULL,
  `last_login` datetime DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `users`
--

INSERT INTO `users` (`id`, `name`, `email`, `password`, `created_at`, `premium_status`, `premium_expiry`, `last_login`) VALUES
(1, 'string', 'user@example.com', '$2y$10$PxwS06xxLI5eawEZ.XPLnOltWzY9z9qLvBHd7dvbJry9Nuwt0fBgi', '2026-02-28 09:34:10', 1, '2026-06-17', '2026-02-28 22:29:58'),
(2, NULL, 'testpostman1@example.com', '$2b$12$AJxEeXyV8M.itXKRWmoVXOWWMU9Pq8w8OTZd35CYzSa1cRYnQig9u', '2026-03-18 01:25:40', 0, NULL, NULL),
(3, NULL, 'vsm2004don2006@gmail.com', '$2b$12$wuebNFNlqc5Wpq7v6QUtV.f/fhOFA9VoO4sxyssA8kpqtfvcvLY42', '2026-03-18 01:30:33', 0, NULL, '2026-03-19 04:21:36'),
(4, NULL, 'manu2004@gmail.com', '$2b$12$C7dwNFdW0N3U6lhpqmpDo.CvTm1I1CUBU0vGBX7/4yWKg/IoxfdQ2', '2026-03-18 22:33:39', 0, NULL, NULL),
(5, NULL, 'manusai1897@gmail.com', '$2b$12$BuXNoSeKoCiQ9SssKUn5/OCdzmzaKPfa3aMaORRLCckskrsecriti', '2026-03-18 23:03:26', 0, NULL, '2026-03-19 18:57:43'),
(6, 'Arul', 'unamias4002@gmail.com', '$2b$12$oORIjGlnj2bJFHCxihtsyu8TXRwMUDPqgJdOQq7ghRkqGdCFRlLEm', '2026-03-19 14:21:34', 0, NULL, '2026-03-28 03:05:20'),
(7, 'manu', 'velampatisaimanu5112.sse@saveetha.com', '$2b$12$NnK4uFMZ6oUMaivac9wrE.83lwGeTkyFflD67VD8qONMkXuV0fKIW', '2026-03-20 01:15:44', 0, NULL, NULL),
(8, NULL, 'Srk1965@gmail.com', '$2b$12$8n1Wwl2lhOqRnaIyWS6GnOhfvAResPqpbIygIBetTZi.Y67k9qcVi', '2026-03-27 03:26:20', 0, NULL, '2026-03-27 08:57:15'),
(9, 'manu sai', 'v.s.manu2004@gmail.com', '$2b$12$o.1hKrQTaf5F2k0rjSavougFnfmFvHsHsE5H0UFC3QpU7uURYoOqG', '2026-03-27 12:01:49', 0, NULL, '2026-03-28 06:37:06'),
(10, NULL, 'velampatisaimanu2004@gmail.com', '$2b$12$Y4RKQ0ur0KIkD3cdVD1oneCRzp.RZJNlStdKwjRn8OYdju28Efehi', '2026-03-28 01:32:58', 0, NULL, NULL),
(11, NULL, 'kiran2006@gmail.com', '$2b$12$nhXDxLFydN9BDbzk.2/H9OVv2auZn6Ix2IF9EuM64mhMQYOTHV24C', '2026-03-28 01:38:32', 0, NULL, NULL),
(12, NULL, 'Manu2007@gmail.com', '$2b$12$mZ3jA83idFPQb7wMrB/souhDkle/OwvgtaNfTUdofb//ykX5uZ7YC', '2026-03-28 01:53:30', 0, NULL, '2026-03-28 07:24:36');

--
-- Indexes for dumped tables
--

--
-- Indexes for table `feedback`
--
ALTER TABLE `feedback`
  ADD PRIMARY KEY (`id`),
  ADD KEY `user_id` (`user_id`);

--
-- Indexes for table `sessions`
--
ALTER TABLE `sessions`
  ADD PRIMARY KEY (`id`),
  ADD KEY `user_id` (`user_id`);

--
-- Indexes for table `users`
--
ALTER TABLE `users`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `email` (`email`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `feedback`
--
ALTER TABLE `feedback`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=3;

--
-- AUTO_INCREMENT for table `sessions`
--
ALTER TABLE `sessions`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=7;

--
-- AUTO_INCREMENT for table `users`
--
ALTER TABLE `users`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=13;

--
-- Constraints for dumped tables
--

--
-- Constraints for table `feedback`
--
ALTER TABLE `feedback`
  ADD CONSTRAINT `feedback_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE;

--
-- Constraints for table `sessions`
--
ALTER TABLE `sessions`
  ADD CONSTRAINT `sessions_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
