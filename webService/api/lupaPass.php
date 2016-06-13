<?php
	error_reporting(E_ALL^E_NOTICE^E_WARNING);
	include "koneksi.php";
	$email = $_POST['email'];
	$pass = $_POST['pass'];
	$query=mysql_query("UPDATE user SET password='".md5($pass)."' WHERE email='".$email."'");
	if($query){
			$obj['Error']="False";
	}
	else{
		$obj['Error']="True";
	}
	echo json_encode($obj);
?>