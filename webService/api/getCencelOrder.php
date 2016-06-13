<?php
error_reporting(E_ALL^E_NOTICE);
include "koneksi.php"; 
$token=$_GET['api_key'];
$idOrder=$_GET['id_order'];
$query=mysql_query("SELECT * FROM user where token='".$token."'");
if(mysql_num_rows($query)>0){
	$query=mysql_query("SELECT * from orders where id_order='".$idOrder."'");
	$data = mysql_fetch_array($query);
	if($data['status']=="7"){
		$query=mysql_query("DELETE FROM orders WHERE id_order='".$idOrder."'");
	}
	else{
			$query=mysql_query("UPDATE orders SET status='7', progres='Pesanan telah dibatalkan.' where id_order='".$idOrder."'");
	}
	if($query){
		$obj['isUpdated']="True";
	}
	else{
		$obj['isUpdated']="False";
	}
}
echo json_encode($obj);
?>