<?php
error_reporting(E_ALL^E_NOTICE);
include "koneksi.php"; 
$token=$_GET['api_key'];
$response = array();
$query=mysql_query("SELECT * FROM user where token='".$token."'");
if(mysql_num_rows($query)>0){
	$data_id=mysql_fetch_array($query);
	$data_id['id_user'];
	$query=mysql_query("SELECT * FROM transaksi WHERE id_user='".$data_id['id_user']."' AND status='0' ORDER BY id_transaksi DESC");
	$i=0;
	while($data=mysql_fetch_array($query)){
		$obj['id_transaksi']=$data['id_transaksi'];
		$obj['id_user']=$data['id_user'];
		$obj['jumlah']=$data['jumlah'];
		$obj['via']=$data['via'];
		$obj['tgl']=$data['tgl_transaksi'];
		$obj['ket']=$data['ket'];
		$obj['bukti']=$data['bukti'];
		$obj['status']=$data['status'];
		$response['data'][$i]=$obj;
		$i++;
	}
}
echo json_encode($response);
?>