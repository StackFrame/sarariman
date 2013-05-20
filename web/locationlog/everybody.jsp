<%--
  Copyright (C) 2013 StackFrame, LLC
  This code is licensed under GPLv2.
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta name="viewport" content="width=device-width, initial-scale=1.0"/>
        <link href="../css/bootstrap.css" rel="stylesheet" media="screen"/>
        <link href="../css/bootstrap-responsive.css" rel="stylesheet" media="screen"/>
        <link href="../style/font-awesome.css" rel="stylesheet" type="text/css"/>
        <link href="../css/style.css" rel="stylesheet" media="screen"/>
        <style type="text/css">
            #map-canvas {
                display: block;
                height: 400px;
            }

            /* Keep the bootstrap stuff from interfering with Google Maps stuff.
               See https://github.com/twitter/bootstrap/issues/2410#issuecomment-4342173 */
            #map-canvas img {
                max-width: none;
            }
        </style>

        <script type="text/javascript" src="https://maps.googleapis.com/maps/api/js?sensor=false"></script>
        <script type="text/javascript" src="../jquery/js/jquery-1.7.2.min.js"></script>
        <script type="text/javascript">
            function formatDate(date, fmt) {
                function pad(value) {
                    return (value.toString().length < 2) ? '0' + value : value;
                }
                return fmt.replace(/%([a-zA-Z])/g, function (_, fmtCode) {
                    switch (fmtCode) {
                        case 'Y':
                            return date.getUTCFullYear();
                        case 'M':
                            return pad(date.getUTCMonth() + 1);
                        case 'd':
                            return pad(date.getUTCDate());
                        case 'H':
                            return pad(date.getUTCHours());
                        case 'm':
                            return pad(date.getUTCMinutes());
                        case 's':
                            return pad(date.getUTCSeconds());
                        default:
                            throw new Error('Unsupported format code: ' + fmtCode);
                    }
                });
            }

            function initialize() {
                var StackFrameWorldHeadquarters = new google.maps.LatLng(28.758822, -81.294179);
                var mapCenter = StackFrameWorldHeadquarters;
                var mapOptions = {
                    center: mapCenter,
                    zoom: 12,
                    mapTypeId: google.maps.MapTypeId.ROADMAP
                };

                var map = new google.maps.Map(document.getElementById("map-canvas"), mapOptions);
                var bounds = new google.maps.LatLngBounds(mapCenter, mapCenter);
                var markers = new Array();
                accuracyCircle = new google.maps.Circle({
                    strokeColor: "#FF0000",
                    strokeOpacity: 0.8,
                    strokeWeight: 2,
                    fillColor: "#FF0000",
                    fillOpacity: 0.05
                });

                $.getJSON("latest.jsp", function(entries) {
                    var infowindow = new google.maps.InfoWindow();

                    $.each(entries, function(key, entry) {
                        var latLng = new google.maps.LatLng(entry.latitude, entry.longitude);
                        bounds.extend(latLng);
                        var image = {
                            url: entry.icon,
                            anchor: new google.maps.Point(25, 25),
                            scaledSize: new google.maps.Size(50, 50)
                        }

                        var marker = new google.maps.Marker({
                            position: latLng,
                            title: entry.employeeDisplayName,
                            icon: image,
                            zIndex: 0
                        });
                        markers.push(marker);

                        // FIXME: Convert to users's preferred timezone.
                        var formattedTimestamp = formatDate(new Date(entry.timestamp), '%M-%d %H:%m:%s') + " UTC";

                        var openInfoWindow = function() {
                            var content = "<p>" + entry.employeeDisplayName + "<br/>" + "Last seen: " +
                                formattedTimestamp + "</p>";

                            infowindow.setContent(content);
                            infowindow.open(map, marker);
                        };

                        var selectMarker = function() {
                            accuracyCircle.setCenter(latLng);
                            accuracyCircle.setRadius(entry.accuracy);
                            accuracyCircle.setMap(map);
                            markers.forEach(function(val, index, a) {
                                val.setZIndex(0);
                            });
                            openInfoWindow();
                            marker.setZIndex(1);
                        };

                        google.maps.event.addListener(marker, 'click', selectMarker);
                        marker.setMap(map);

                        var nameCell = $('<td>').append(entry.employeeDisplayName);
                        var lastSeenCell = $('<td>').append(formattedTimestamp);
                        var row = $('<tr>').append(nameCell);
                        row.append(lastSeenCell);
                        $('#locations > tbody:last').append(row);
                        row.on('click', null, function() {
                            selectMarker();
                            map.setZoom(17);
                            map.panTo(marker.position);
                        });
                    });
                    map.fitBounds(bounds);
                });
            }
            google.maps.event.addDomListener(window, 'load', initialize);
        </script>
        <title>Latest Employee Locations</title>
    </head>
    <body>
        <%@include file="/WEB-INF/jspf/navbar.jspf" %>

        <div class="container">

            <h1>Latest Employee Locations</h1>

            <div id="map-canvas" class="span12">
            </div>

            <div>
                <table id="locations" class="table table-bordered table-striped table-rounded">
                    <thead>
                        <tr>
                            <th>Employee</th>
                            <th>Last Seen</th>
                        </tr>
                    </thead>
                    <tbody>
                    </tbody>
                </table>
            </div>

            <%@include file="../footer.jsp" %>
        </div>

    </body>
</html>