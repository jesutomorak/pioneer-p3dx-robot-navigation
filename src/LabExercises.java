import org.jfree.ui.RefineryUtilities;
//import robot.Mapping;
import robot.Robot;
import robot.Sensor;
import utils.Delay;
import utils.ScatterPlotter;

import java.awt.*;
import java.util.Arrays;

/**
 * Created by Theo Theodoridis.
 * Class    : LabExercises
 * Version  : v1.0
 * Date     : © Copyright 20-01-2015
 * User     : ttheod
 * email    : ttheod@gmail.com
 * Comments : The class contains lab example solutions for the Term assignment.
 **/

public class LabExercises
{
    private Robot robot;
    private ScatterPlotter scatterPlotter;

     /**
    * Method     : LabExercises::LabExercises()
    * Purpose    : Default LabExercises class constructor.
    * Parameters : robot : An object of Class Robot.
    * Returns    : Nothing.
    * Notes      : None.
    **/
    public LabExercises(Robot robot)
    {
        this.robot = robot;

        scatterPlotter = new ScatterPlotter("Laser Scanner", "Scatter Plot", "X", "Y", "Data", Color.BLUE, false);
        scatterPlotter.pack();
        RefineryUtilities.centerFrameOnScreen(scatterPlotter);
        scatterPlotter.setVisible(true);
    }

    private static final double GAMMA_A  = 200.0;      //the error which can be adjusted for detection
    private static final double GAMMA_TH = 1.5;        //error for desired orientation
    private static final double GAMMA_D  = 80.0;       //error for distance away from coordinates

   /**
    * Method     : LabExercises::navigate()
    * Purpose    : To implement waypoint navigation method using an odometry/trajectory model.
    * Parameters : - vel : The robot velocity.
    * Returns    : True if navigation is completed (last node), False otherwise.
    * Notes      : None.
    **/


    private static final int TURN = 0;                //TURN value given for Switch function
    private static final int MOVE = 1;                //MOVE value given for Switch function
    private static final int AVOID = 2;

    private int step = TURN;                          //initialise the step value for Switch function to TURN
    private int i = 0;                                //initialise node counter to 0 for MOVE step


    private static double NODE[][] =           //coordinates for each point for my robot to move
    {                                          //A large number of nodes is used to correct course from motion error
        {2200, 0},  //node 1
        {2200,-2350}, //node 2
        {3000,-2600},
        {4000,-2600}, //node 3
        {4000,-3800}, //node 4
        {-1700,-3800}, //node 5

    };

    public boolean navigate(double vel)    //Odometry model for Robot
    {
        // [1]Extract node coordinates:
        double x  = NODE[i][0];
        double y  = NODE[i][1];
        double th = getAngle(x, y);                    //destination orientation angle
        double rTh = get360(robot.arRobot.getTh());    //current orientation angle

        /*double l_vec[] = {
                robot.sensor.getSonarRange(1),
                robot.sensor.getSonarRange(2),
                robot.sensor.getSonarRange(3)
        };
        double r_vec[] = {
                robot.sensor.getSonarRange(4),
                robot.sensor.getSonarRange(5),
                robot.sensor.getSonarRange(6),
        };

        //Calculate the left/right min sensor vector:
        double l_min = Math.min(Math.min(l_vec[0], l_vec[1]), l_vec[2]);  // only checking the front 6 sensors
        double r_min = Math.min(Math.min(r_vec[0], r_vec[1]), r_vec[2]); */

        switch(step)
        {
            // [2]Turn and stop to the node's location:
            case TURN:
            {
                double deltaTh = th-rTh; //the difference between destination orientation and current orientation
                boolean turnLeft = (rTh<=180 && deltaTh>0 && deltaTh<180)||(rTh>180 && (deltaTh>0 || deltaTh<-180)); //turn left conditions

                if (turnLeft)  //if turn left conditions met
                {
                    robot.control.turnSpot(-vel/3);          //turn left
                } else {                                     //if turn left conditions not met
                    robot.control.turnSpot(vel/3);           //turn right
                }

                if(isAngularDestination(th))       //when destination orientation is reached
                {
                    robot.control.stop();          //stop
                    step = MOVE;                   //change to MOVE step
                }
            }break;                                //leave Switch function

            // [3]Move and stop to the node's location:
            case MOVE:
            {
              /*  if (l_min<GAMMA_A || r_min<GAMMA_A)
                {
                    robot.control.stop();
                    step = AVOID;
                    break;
                } else {     */
                robot.control.move(vel);            //move forward
                if(isLinearDestination(x, y))       //when coordinates have been reached
                {
                    robot.control.stop();           //stop
                    step = TURN;                    //change to TURN step
                    if(++i==NODE.length)          // i increments and and if i = the number of node coordinates
                    {
                        return(true);              //navigate function returns true, meaning it has reached the final node
                    }

                }

            }break;                                //leave switch function

            case AVOID:
            {
             if (!avoid(vel))
             {
                 step = TURN;                    //change to TURN step
             }
            }break;
        }
        return(false);                             //navigate function returns false
    }

    public boolean inKitchen(double x)
    {
    if(x<-1700){return(true);}
    else{return(false);}
    }

    public boolean endZone(double x, double y)
    {
        if(x<-2100 && y>-500){return(true);}
        else{return(false);}
    }

    public double getAngle(double nX, double nY)    //finding the destination orientation angle to reach the next node
    {
        return(get360(Math.toDegrees(Math.atan2(nY - robot.kinematics.getY(), nX - robot.kinematics.getX()))));
    }

    public double get360(double th)                    //Converting atan2 values to the 0-360 range
    {
        return(th - 360.0 * Math.floor(th / 360.0));   //Could also use the modulo function
    }

    public boolean isAngularDestination(double nTh)    //function to compare desired and current orientation
    {
        double rTh = get360(robot.arRobot.getTh());                  //Converting robot angle values to the 0-360 range
        if((rTh >= (nTh - GAMMA_TH)) && (rTh <= (nTh + GAMMA_TH)))   //If current angle is within GAMMA_TH from desired angle
        return(true);                                           //return true
        return(false);                                          // else false
    }

    public boolean isLinearDestination(double nX, double nY)    //function to compare desired and current position
    {
        double d = Math.sqrt(Math.pow(nX - robot.kinematics.getX(), 2) + Math.pow(nY - robot.kinematics.getY(), 2));
        if(d <= GAMMA_D)      //If current position is within GAMMA_D from desired position
        return(true);         //return true
        return(false);        // else false
    }

   /**
    * Method     : LabExercises::avoid()
    * Purpose    : To implement an obstacle avoidance and collision detection algorithm. The escape method is not implemented here.
    * Parameters : - vel : The robot velocity.
    * Returns    : true if an obstacle is detected, false otherwise.
    * Notes      : None.
    **/
    public boolean avoid(double vel)
    {
        double l_vec[] = {
                robot.sensor.getSonarRange(0),
                robot.sensor.getSonarRange(1),
                robot.sensor.getSonarRange(2),
                robot.sensor.getSonarRange(3)
        };
        double r_vec[] = {
                robot.sensor.getSonarRange(4),
                robot.sensor.getSonarRange(5),
                robot.sensor.getSonarRange(6),
                robot.sensor.getSonarRange(7)
        };

        Arrays.sort(l_vec);  //sort the left array measurements in ascending order
        Arrays.sort(r_vec);  //sort the right array measurements in ascending order

        //Define the left/right min sensor vectors:

        double l_min = l_vec[0];  //smallest value
        double l_min2 = l_vec[1]; //2nd smallest value
        double r_min = r_vec[0];  //smallest value
        double r_min2 = r_vec[1]; //2nd smallest value

        // Untrap from corners:
        if (l_min < GAMMA_A && l_min2 < GAMMA_A && r_min <GAMMA_A)  //If 2 left sensors and one right sensor are below GAMMA_A,
        {
            robot.control.turnSpot(vel/3);                          //turn right
            return(true);
        } else
        if (r_min < GAMMA_A && r_min2 < GAMMA_A && l_min <GAMMA_A) //If 2 right sensors and one left sensor are below GAMMA_A,
        {
            robot.control.turnSpot(-vel/3);                        //turn left
            return(true);
        } else

        // otherwise Avoid:
        if(l_min < GAMMA_A )                  //if an obstacle is sensed to the left within a distance of GAMMA_A,
        {
            robot.control.turnSpot(vel/3);    //turn right
            return(true);
        } else

        if(r_min < GAMMA_A )                  //if an obstacle is sensed to the right within a distance of GAMMA_A,
        {
            robot.control.turnSpot(-vel/3);   //turn left
            return(true);
        } else

        // otherwise decollide:
        if (l_min>GAMMA_A && r_min>GAMMA_A)       // If sensors don't detect anything within GAMMA_A
        {
          boolean initPose = (robot.kinematics.getX() == 0) && (robot.kinematics.getY() == 0); //true when at start position
          boolean zeroVel  = (robot.kinematics.getLeftVel() == 0) && (robot.kinematics.getRightVel() == 0); //true when speed is 0

          if(zeroVel && !initPose)    //if speed is 0 and not in start position (robot is stuck or has crashed)
          {
              robot.control.move(-vel/3); // Move backward.
              Delay.ms(1000);

              double p = Math.random();                 //turn left or right at random
              if(p < 0.5) robot.control.turnSpot(vel/3);
              else        robot.control.turnSpot(-vel/3);
              Delay.ms(1000);
          } else
          // otherwise move forward:
          {
              robot.control.move(vel); // Move forward.
          }
        }

        return(false);
    }

   /**
    * Method     : LabExercises::track()
    * Purpose    : To perform target tracking using 3 discrete zones.
    * Parameters : - vel : The robot velocity.
    * Returns    : True if detecting a target, false otherwise.
    * Notes      : Make use of the camera sensors and blob detector.
    **/
    public boolean track(double vel)
    {
        // [1]Validate left image zone and turn left:
        if((robot.sensor.getBlobX() > 0) && (robot.sensor.getBlobX() < (robot.sensor.getImageHeight() / 4)))     //changed image width to height
        {
            robot.control.turnSharp(-vel/3);    //turn left
            System.out.println("< L <");
            return(true);
        }
        else
        // [2]Validate right image zone and turn right:
        if((robot.sensor.getBlobX() > ((3 * robot.sensor.getImageHeight()) / 4)) && (robot.sensor.getBlobX() < robot.sensor.getImageHeight())) //changed image width to height
        {
            robot.control.turnSharp(vel/3);
            System.out.println("> R >");        //turn right
            return(true);
        }
        else
        // [3]Alternatively, approach target:
        {
			double f_vec[] = {robot.sensor.getSonarRange(3), robot.sensor.getSonarRange(4)};
			double min = Math.min(f_vec[0], f_vec[1]);

			if(min <= GAMMA_A) robot.control.stop();
			else			   robot.control.move(vel/2);

            System.out.println("| M |");
            return(false);
        }
    }

   /**
    * Method     : LabExercises::mapBuilder()
    * Purpose    : To implement a mapping algorithm using the robot's front sonar ring.
    * Parameters : None.
    * Returns    : Nothing.
    * Notes      : The algorithm builds a 2D map and displays it.
    **/
    public void mapBuilder()
    {
        for(int i=0 ; i<8 ; i++)
        {
            // [1]Acquire sensor characteristics and range:
            double sonarR  = robot.sensor.getSonarRange(i);
            double sonarX  = robot.sensor.getSonarX(i);
            double sonarY  = robot.sensor.getSonarY(i);
            double sonarTh = Math.toRadians(robot.sensor.getSonarTh(i));
            double robotTh = Math.toRadians(robot.kinematics.getTh());

            // [2]Filter values detected outside sensor boundaries:
            if((sonarR > 100.0) && (sonarR < 5000.0))
            {
                // [2.1]Calculate sonar detected local instance:
                double Xp = sonarX + Math.cos(sonarTh) * sonarR;    //x coordinate of the object detected with reference to the sensor
                double Yp = sonarY + Math.sin(sonarTh) * sonarR;    //y coordinate of the object detected with reference to the sensor

                // [2.2]Calculate the trigonometric components for the robot's left/right side sonars:
                double Xg = Xp * Math.cos(robotTh) - Yp * Math.sin(robotTh);    //x coordinate of the object detected with reference to the robot
                double Yg = Xp * Math.sin(robotTh) + Yp * Math.cos(robotTh);    //y coordinate of the object detected with reference to the robot

                // [2.3]Calculate sonar detected global instance:
                Xg = Xg + robot.kinematics.getX();
                Yg = Yg + robot.kinematics.getY();

                // [2.4]Plot point map instances:
                scatterPlotter.series.add(Xg, Yg);
                System.out.printf("\n%.1f\t%.1f", Xg, Yg);
            }
        }
        // [3]Plot robot trajectory:
        scatterPlotter.series.add(robot.kinematics.getX(), robot.kinematics.getY());
    }
}
