<?php
if($_SERVER['REQUEST_METHOD']=='POST'){
		//MEndapatkan Nilai Dari Variable 
		$id = $_POST['id'];
		$name = $_POST['name'];
		$position = $_POST['position'];
		$salary = $_POST['salary'];
		
		//import file koneksi database 
		require_once('koneksi.php');
		
		//Membuat SQL Query
		$sql = "UPDATE tb_pegawai SET nama = '$name', posisi = '$position', gaji = '$salary' WHERE id = $id;";
		
		//Meng-update Database
		if (mysqli_query($con, $sql)) {
			echo json_encode(array('status' => 'OK', 'message' => 'Berhasil Update Data Pegawai'));
		} else {
			echo json_encode(array('status' => 'KO', 'message' => 'Gagal Update Data Pegawai'));
		}
		
		mysqli_close($con);
	}
?>