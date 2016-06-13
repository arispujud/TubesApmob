<?php
error_reporting(E_ALL^E_NOTICE);
include "koneksi.php"; 
$token=$_GET['api_key'];
$idOrder=$_GET['id_order'];
$response = array();
$query=mysql_query("SELECT * FROM user where token='".$token."'");
if(mysql_num_rows($query)>0){
	$query=mysql_query("SELECT * FROM orders where id_order='".$idOrder."'");
	$i=0;
	while($data=mysql_fetch_array($query)){
		$obj['id_order']=$data['id_order'];
		$obj['id_user']=$data['id_user'];
		$obj['subject']=$data['subject'];
		$obj['jenis']=$data['jenis'];
		$obj['bahan']=$data['bahan'];
		$obj['harga']=$data['harga'];
		$obj['jumlah']=$data['jumlah'];		
		$obj['alamat']=$data['alamat_kirim'];
		$obj['keterangan']=$data['keterangan'];
		$x=explode(',',$data['desain']);
		$obj['desain']=$x[0];
		$obj['progres']=$data['progres'];
		$obj['status']=$data['status'];
		$obj['tgl']=$data['tgl_order'];
		$obj['jam']=$data['jam_order'];
		//$response['data'][$i]=$obj;
		$i++;
	}
}
echo json_encode($obj);
?>