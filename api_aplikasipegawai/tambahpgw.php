<?php
	//Import File Koneksi database
	require_once('koneksi.php');
	$target_path = dirname(__FILE__).'/uploads/';
	$_BASE_URL = 'http://192.168.1.4/aplikasipegawai/uploads/';

	if($_SERVER['REQUEST_METHOD']=='POST'){
		
		//Mendapatkan Nilai Variable
		$name = $_POST['name'];
		$position = $_POST['position'];
		$salary = $_POST['salary'];

		$target_path = $target_path . basename($_FILES['image']['name']);

		try{
			if(!move_uploaded_file($_FILES['image']['tmp_name'], $target_path)){
				echo json_encode(array('status'=>'KO', 'message'=> 'Image gagal diupload'));
			}else {
				$sql = "INSERT INTO tb_pegawai (nama,posisi,gaji,image) VALUES ('$name','$position','$salary','".$_BASE_URL.basename($_FILES['image']['name']) ."')";

				//Eksekusi Query database
				if (mysqli_query($con, $sql)) {
					echo json_encode(array('status' => 'OK', 'message' => 'Berhasil Menambahkan Pegawai'));
				} else {
					echo json_encode(array('status' => 'KO', 'message' => 'Gagal Menambahkan Pegawai'));
				}
				mysqli_close($con);
			}

		} catch(Exception $e){
			echo json_encode(array('status'=>'KO', 'message'=> $e->getMessage()));
		}
		
	}
?>