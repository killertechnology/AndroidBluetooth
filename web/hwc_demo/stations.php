<html><head>

</head><?php include("header.php"); ?>
<div style='font-size:25px;'>HWC Stations</div>
	

<table width="877" border=1 cellpadding=6 
<tr>
<td valign="top" width="1%"><?php include("menu.php"); ?></td>
<td valign="top">
<?php

	header('Content-type: text/html');

	/* grab the posts from the db */
	$query = "SELECT ID, StationName, StationLocation, StationFloor "
	.$query = "FROM hwcdemo.RFID_Stations ";
	
	/* connect to the db */
	$link = mysql_connect('hwcdemo.db.7516722.hostedresource.com','hwcdemo','thqgwq9R2RKh!') or die('Cannot connect to the DB');
	mysql_select_db('hwcdemo',$link) or die('Cannot select the DB');
	
	$result = mysql_query($query,$link) or die('Errant query:  '.$query);
    $format = 'json'; //json

    echo("<table border=0 cellspacing=1 cellpadding=2 style='font-family:Calibri;'>");
	echo ("<tr style='font-weight:bold;background-color:#CCD2DE;color:#334C7D;'>");
		echo("<td style='width:1%;' nowrap>Station ID</td>");
		echo("<td style='width:150px';>Station Name</td>");
		echo("<td style='width:150px';>Station Location</td>");
		echo("<td style='width:150px';>Station Floor</td>");
	echo("</tr>");

	/* create one master array of the  records */
	$posts = array();
	$index = 0;
	if(mysql_num_rows($result)) {
		while($post = mysql_fetch_assoc($result)) {
			$posts[] = array('post'=>$post);
			echo ("<tr");

			echo(" style='color:white;font-size:14px;'");
			echo("/>");
			echo("<td>" . $post["ID"] . "</td>");
			echo("<td>" . $post["StationName"] . "</td>");
			echo("<td>" . $post["StationLocation"] . "</td>");
			echo("<td>" . $post["StationFloor"] . "</td>");
			
			echo("</tr>");

			$index++;
		}
	}

	echo("</table>");

	/* output in necessary format 
	if($format == 'json') {
		
		//echo json_encode(array('posts'=>$posts));
	
	}
	*/
	/* disconnect from the db */
	@mysql_close($link);

?>
</td>
</tr>
</table>
<br><br><br></body></html>