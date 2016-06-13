<?php
	error_reporting(E_ALL^E_NOTICE^E_WARNING);
	include "koneksi.php";
	$email = $_POST['email'];
	
	$query=mysql_query("SELECT * FROM user where email='".$email."'");
	$data=mysql_fetch_array($query);
	if($data){
		$kode=rand(1000,9999);
		$query=mysql_query("UPDATE user SET kode='".$kode."' WHERE id_user='".$data['id_user']."'");
		if($query){
			$obj['Error']="False";
			$mail = "admin@arispujud.com";
			$to      = $email;
			$subject="GANTI PASSWORD MAYERS CARTENZ" ;
			//$message = $_REQUEST['pesan'] ;
			$message="Halo ".$nama."!\nAnda telah meminta kode verifikasi untuk ganti password.\n Kode Verifikasi : ".$kode." \nJika ini benar permintaan anda, segera masukkan kode tersebut untuk perubahan kata sandi anda." ;

			$headers = 'From:'.$mail . "\r\n" .
				'Reply-To:'.$mail . "\r\n" .
				'X-Mailer: PHP/' . phpversion();

			mail($to, $subject, $message, $headers);
		}
		else{
			$obj['Error']="True";
		}
	}
	else{
		$obj['Error']="True";
	}
	echo json_encode($obj);
?>