# Implementation details

## Program entry point

`Run.java` initialises the robot, `LabExercises`, and the control panel. Its
main loop prints the current odometry, runs waypoint navigation until the
navigation phase is complete, then executes obstacle avoidance and target
tracking until the end-zone condition is met.

## Navigation state logic

`LabExercises.navigate()` uses three integer states:

- `TURN`
- `MOVE`
- `AVOID`

The destination heading is calculated using `atan2`, converted to degrees and
normalised to the range 0–360°. The robot turns on the spot until the heading
falls within `GAMMA_TH`, then moves until its Euclidean distance from the
waypoint is no greater than `GAMMA_D`.

## Route

The robot starts from its initial pose and follows six programmed coordinate
entries:

```text
Start  (0, 0)
W1     (2200, 0)
W2     (2200, -2350)
W3     (3000, -2600)
W4     (4000, -2600)
W5     (4000, -3800)
W6     (-1700, -3800)
```

The final coordinate `(-1700, -3800)` is the N5 reference destination used in
the odometry experiment table. The extra programmed points guide the robot
around the environment and correct its course before the final destination.

## Obstacle avoidance

The avoidance method reads the front sonar sensors in left and right groups,
sorts their measurements and uses the two smallest readings to detect close
obstacles and corner conditions. If the robot is stationary away from its
initial pose, it reverses and chooses a random turning direction.

## Target tracking

The blob detector's horizontal position is divided into three regions. The
robot turns left or right when the target lies near an image edge and moves
toward the target when it lies in the middle region. Front sonar measurements
provide the stopping condition.

## Mapping

For each of the eight front sonar readings, the method:

1. obtains the sensor range, position and orientation;
2. calculates the detected point in local sensor coordinates;
3. rotates the point into the robot coordinate frame;
4. translates it using the robot's global odometry;
5. adds the point to the scatter plot.

The robot's current odometry is also added to the plotted series.
