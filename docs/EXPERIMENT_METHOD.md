# Experimental method

## Phases

The experiment was divided into two phases:

1. **Odometry phase:** the robot navigated from the initial pose to the final
   waypoint, using `(-1700, -3800)` mm as the N5 reference coordinate in the
   experiment table.
2. **Tracking phase:** the robot followed the target from N5 toward the N6
   reference coordinate `(-2100, -500)` mm.

Ten experimental runs were recorded for each phase.

## Measurements

For every run, the following were recorded:

- final x coordinate in millimetres;
- final y coordinate in millimetres;
- Euclidean error from the phase reference coordinate;
- completion time in seconds.

The Euclidean error was calculated as:

```text
d = sqrt((x - x_reference)^2 + (y - y_reference)^2)
```

The experiment table also calculates the arithmetic mean, sample standard
deviation and Pearson correlation coefficient between Euclidean error and
completion time.

The source workbook uses the label `p` for the final coefficient; the formula
and values correspond to Pearson's correlation coefficient, conventionally
written as `r`.
