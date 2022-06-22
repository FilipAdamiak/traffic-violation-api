<!doctype html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, user-scalable=no, initial-scale=1.0, maximum-scale=1.0, minimum-scale=1.0">
    <meta http-equiv="X-UA-Compatible" content="ie=edge">
    <title>Warning about your driving license</title>
</head>
<body>
<div>Hi, <b>${violation.personData}</b></div>
<br/>
<div style="margin-top: 10px">You lost your driving license because of new traffic ticket:</div>
<br/>
<div>Date: <b>${violation.date}</b></div>
<br/>
<div>Points: <b>${violation.points}</b></div>
<br/>
<div>Payment: <b>${violation.payment}</b></div>
<br/>
<div>Violation type: <b>${violation.type}</b></div>
<br/>
<div>Your total points in the previous year: <b>${violation.totalPoints}</b></div>
<br/>
<div>You are obligated to pay the ticket in 7 days!</div>
<br/>
</body>
</html>