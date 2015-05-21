<?php

	header('Content-type: text/html');


	/* connect to the db */
	$link = mysql_connect('hwcdemo.db.7516722.hostedresource.com','hwcdemo','thqgwq9R2RKh!') or die('Cannot connect to the DB');
	mysql_select_db('hwcdemo',$link) or die('Cannot select the DB');

	/* grab the posts from the db */
	$query = "SELECT a.id, a.rfid_id, a.date_created, b.firstName, b.lastName,b.videoURL "
	.$query = "FROM hwcdemo.ActivationRecords a, hwcdemo.RFID_Users b WHERE a.rfid_id = b.rfid_id "
	.$query = "ORDER by a.date_created DESC";
	
	$result = mysql_query($query,$link) or die('Errant query:  '.$query);
    $format = 'json'; //json

    echo("<META http-equiv='refresh' content='18;URL=http://www.popordrop.com/index.php'>");
    echo("<table border=1 cellspacing=0 cellpadding=2 style='width:80%;font-family:Calibri;'>");
	echo ("<tr style='font-weight:bold;background-color:lightblue;'>");
		echo("<td style='width:50px';>ID</td>");
		echo("<td style='width:150px';>First</td>");
		echo("<td style='width:150px';>Last</td>");
		echo("<td style='width:255px';>RFID #</td>");
		echo("<td style='width:455px';>videoURL</td>");
		echo("<td style='width:555px';>date_created</td>");
		echo("</tr>");

	/* create one master array of the records */
	$posts = array();
	$index = 0;
	if(mysql_num_rows($result)) {
		while($post = mysql_fetch_assoc($result)) {
			$posts[] = array('post'=>$post);
			echo ("<tr");

			if ($index == 0){
				echo(" style='color:red;font-size:20px;'");
			}
			else if ($index == 1){
				echo(" style='color:green;font-size:18px;'");
			}
			else{
				echo(" style='color:blue;font-size:12px;'");
			}
			
			echo("/>");
			echo("<td>" . $post["id"] . "</td>");
			echo("<td>" . $post["firstName"] . "</td>");
			echo("<td>" . $post["lastName"] . "</td>");
			echo("<td>" . $post["rfid_id"] . "</td>");
			echo("<td>" . $post["videoURL"] . "</td>");
			echo("<td>" . $post["date_created"] . "</td>");
			
			echo("</tr>");

			$index++;
		}
	}

	echo("</table><br><br><br>");

	/* output in necessary format 
	if($format == 'json') {
		
		//echo json_encode(array('posts'=>$posts));
		


	}
	*/
	/* disconnect from the db */
	@mysql_close($link);


?>