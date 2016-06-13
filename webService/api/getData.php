<?php
	error_reporting(E_ALL^E_NOTICE);
	include "koneksi.php";
	$token=$_POST['api_key'];	
	$query=mysql_query("SELECT * FROM user where token='".$token."'");
	
	if(mysql_num_rows($query)>0){
		$data=mysql_fetch_array($query);
		echo "{\"Id\":\"".$data['id_user']."\",\"Nama\":\"".$data['nama']."\",\"Email\":\"".$data['email']."\"}";
	}
	else{
		echo "{\"Id\": \"Failed\"}";
	}
	
?>