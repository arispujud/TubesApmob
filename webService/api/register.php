<?php
	error_reporting(E_ALL^E_NOTICE^E_WARNING);
	include "koneksi.php";
	
	$nama = $_POST['nama'];
	$password = $_POST['password'];
	$email = $_POST['email'];
	$alamat = $_POST['alamat'];
	$tlp = $_POST['tlp'];
	$idline = $_POST['line'];
	
	$password=md5($password);
	
	$query=mysql_query("SELECT * FROM user where email='".$email."'");
	$data=mysql_num_rows($query);
	if($data>0){
		echo "Failed";
	}
	else{
		$query=mysql_query("INSERT INTO user (nama,password,email,alamat,token,telepon,id_line) VALUES ('".$nama."','".$password."','".$email."','".$alamat."','notset','".$tlp."','".$idline."')");
		if($query){
			echo "Success";
			$mail = "admin@arispujud.com";
			$to      = $email;
			$subject="AKTIFASI AKUN MAYERS CARTENZ" ;
			//$message = $_REQUEST['pesan'] ;
			$message="Halo ".$nama."!\nEmail anda telah didaftarkan sebagai member Mayers Cartenz.\nSilahkan melakukan aktifasi akun dengan mengunjungi alamat berikut http://mayerscartensz.esy.es/api/activation.php/?em=".$email."&nm=".md5($nama).".\nJika anda tidak merasa mendaftarkan diri ke Mayers Cortenz, silahkan abaikan pesan ini." ;

			$headers = 'From:'.$mail . "\r\n" .
				'Reply-To:'.$mail . "\r\n" .
				'X-Mailer: PHP/' . phpversion();

			mail($to, $subject, $message, $headers);
		}
		else{
			echo "Failed";
		}
	}
?>