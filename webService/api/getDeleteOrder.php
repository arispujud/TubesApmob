<?php
error_reporting(E_ALL^E_NOTICE);
include "koneksi.php"; 
$token=$_GET['api_key'];
$idOrder=$_GET['id_order'];
$query=mysql_query("SELECT * FROM user where token='".$token."'");
if(mysql_num_rows($query)>0){
	$query=mysql_query("DELETE FROM orders where id_order='".$idOrder."'");
	if($query){
		$obj['isDeleted']="True";
	}
	else{
		$obj['isDeleted']="False";
	}
}
echo json_encode($obj);
?>