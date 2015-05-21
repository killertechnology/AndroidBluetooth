 <?php

//Set the connection string for the db
$conn = mysql_connect('hwcdemo.db.7516722.hostedresource.com','hwcdemo','thqgwq9R2RKh!') or die('Cannot connect to the DB');
//$conn = mysql_connect('hwcdemo.db.7516722.hostedresource.com','hwcdemo','e@r3aucGeZqUx4p') or die('Cannot connect to the DB');


//Show an error if no connection exists
if (!$conn) { die('Could not connect: ' . mysql_error()); }
 
//select the db once connection is opened
mysql_select_db('hwcdemo',$conn) or die('Cannot select the DB');

//Capture the handwash duration during an active event

  if ($_GET['eventType'] == "newSession"){
        $sql1="INSERT INTO hwcdemo.ActivationRecords (rfid_id) VALUES ('$_GET[rfid_id]')";
        
        //Send capture request
        if (!mysql_query($sql1,$conn)) { die('Error: ' . mysql_error()); }

  }
  else if ($_GET['eventType']=="pedal"){
        $sql1="INSERT INTO hwcdemo.ActivationRecords (rfid_id,duration) "
        .$sql1="VALUES ('$_GET[rfid_id]','$_GET[duration]') " 
        .$sql1="WHERE session_id = " . '$_GET[session_id]' . "";

        //Show db error if encountered 
        if (!mysql_query($sql1,$conn)) { die('Error: ' . mysql_error()); }

  }
  else if ($_GET['eventType']=="getVideoURL"){
        //Start the session and capture RFID
        $sql2="SELECT videoURL, id as sessionID FROM hwcdemo.RFID_Users where RFID_Users.rfid_id = ('$_GET[rfid_id]') LIMIT 1";
        //echo $sql2;

        $result = $conn->query($sql2);
        if ($result->num_rows > 0) {
            // output data of each row
            while($row = $result->fetch_assoc()) {
                //echo "id: " . $row["videoURL"]. "<br>";
                echo $row["videoURL"];
                echo "|";
                echo $row["sessionID"]; 
            }
        } else {
            echo "0 results";
        }
  }


//$conn->close();
die();

?>
