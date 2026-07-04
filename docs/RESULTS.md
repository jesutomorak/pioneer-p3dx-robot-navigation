# Experimental results

## Summary

| Phase | Mean x (mm) | Mean y (mm) | Mean error (mm) | Error SD (mm) | Mean time (s) | Time SD (s) | Pearson r |
|---|---:|---:|---:|---:|---:|---:|---:|
| Odometry | -1693.7 | -3697.6 | 120.98 | 45.83 | 177.8 | 1.75 | 0.468 |
| Tracking | -2454.2 | -498.1 | 354.21 | 158.85 | 121.3 | 0.95 | 0.502 |

## Interpretation

The odometry phase produced a lower mean endpoint error and substantially lower
error variability than the tracking phase. Tracking completion time was shorter
and less variable, while final position accuracy varied more strongly between
runs.

The distance–time correlation was positive in both phases:

- Odometry: `r = 0.468`
- Tracking: `r = 0.502`

With only ten observations per phase, these coefficients are presented as
descriptive experimental results rather than broad statistical conclusions.

## Figures

- [`planned_odometry_route.png`](../results/planned_odometry_route.png)
- [`odometry_final_positions.png`](../results/odometry_final_positions.png)
- [`tracking_final_positions.png`](../results/tracking_final_positions.png)
- [`odometry_distance_time_correlation.png`](../results/odometry_distance_time_correlation.png)
- [`tracking_distance_time_correlation.png`](../results/tracking_distance_time_correlation.png)
