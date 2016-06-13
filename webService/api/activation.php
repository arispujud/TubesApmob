<?php
	error_reporting(E_ALL^E_NOTICE);
	include "koneksi.php";
	
	$email=$_GET['em'];
	$nama = $_GET['nm'];
	
	$token=md5($email);
	
	$query=mysql_query("UPDATE user SET token='".$token."' WHERE email='".$email."'");
	if($query){
		echo "Success";
	}
	else{
		echo "Failed";
	}
?>