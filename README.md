# SpeedoTracker_Android
An application displaying speed information when user is driving (instant speed, average speed on a section).

# Functional part: How to use the app?
Start "SpeedoTracker" application.
The application starts on the main screen.

## Start/stop speed recording
A round button allows to start and stop recording speed.
When starting recording, the app checks if all conditions are fulfilled to start recording.
If it is not the case, the app displays information in a popup about what the user has to do to record.

*Note: application may not directly display the instant speed when record is started. User has to wait for fine location tracking.*

## Speed information
On the main screen, the instant speed is displayed.
A modal appears from the bottom to display the average speed when speed is not enough. **The minimum speed to show the instant speed must be more than 5km/h.**
The average speed is computed on the last section. A section starts when the speed is upper than (or equals to) 5 km/h. A section stops when the speed is lower than 5km/h.

# Technical part: under the hood
The application is designed following Model-View-Presenter architecture.
The application is composed of a main activity called `SpeedTrackingActivity`. This activity orchestrates the display of instant speed screen and feedback screen.
It registers a `SpeedTrackingPresenter` dealing with the model layer and update the UI supported by the activity.

Two screens of activity are handled by fragments: a `InstantSpeedFragment` and a `FeedbackFragment`. The activity initializes them through a controller called `SpeedTrackingController`.
The `InstantSpeedFragment` is contained in the main container of the activity.
The `FeedbackFragment` is contained in another container that is displayed as a bottom sheet.
Each fragments register their own presenters. `InstantSpeedFragment` uses `InstantSpeedPresenter` and `FeedbackFragment` uses `FeedbackPresenter`.

All the three presenters register to the model layer to get information about speed.
The model layer is mainly composed of a `GpsLocationProvider` extending `LocationProvider` which defines the base behavior and every accessors for speed recording.
`GpsLocationProvider` needs a `GpsLocationCallback` to deal with the system component (such as `LocationManager`).
