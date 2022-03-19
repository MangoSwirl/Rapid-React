// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.subsystems;

import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.SupplyCurrentLimitConfiguration;
import com.ctre.phoenix.motorcontrol.TalonFXControlMode;
import com.ctre.phoenix.motorcontrol.can.TalonFX;
import com.revrobotics.ColorSensorV3.RawColor;

import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.DriverStation.Alliance;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants;
import frc.robot.components.Falcon;
import frc.robot.components.LazyTalonFX;
import io.github.oblarg.oblog.Loggable;
import io.github.oblarg.oblog.annotations.Log;

public class RoutingSubsystem extends SubsystemBase implements Loggable {
  public DigitalInput lowerBeambreak = new DigitalInput(Constants.LOWER_BEAMBREAK);
  public DigitalInput upperBeambreak = new DigitalInput(Constants.UPPER_BEAMBREAK);
  public final TalonFX innerFeeder = new LazyTalonFX(Constants.INNER_FEEDER_MOTOR);
  public final TalonFX outerFeeder = new LazyTalonFX(Constants.OUTER_FEEDER_MOTOR);
  PIDController innerFeederPID = new PIDController(0.05, 0.0, 0);
  PIDController outerFeederPID = new PIDController(0.05, 0.0, 0);
  RawColor color = new RawColor(0, 0, 0, 0);
  double saturation = 0;
  /** Creates a new RoutingSubsystem. */
  public RoutingSubsystem() {
    innerFeeder.config_kP(0, innerFeederPID.getP());
    innerFeeder.config_kI(0, innerFeederPID.getI());

    outerFeeder.config_kP(0, outerFeederPID.getP());
    outerFeeder.config_kI(0, outerFeederPID.getI());

    innerFeeder.configSupplyCurrentLimit(new SupplyCurrentLimitConfiguration(true, 20, 10, 0.25));
    outerFeeder.configSupplyCurrentLimit(new SupplyCurrentLimitConfiguration(true, 20, 10, 0.25));

    innerFeeder.setNeutralMode(NeutralMode.Brake);
    outerFeeder.setNeutralMode(NeutralMode.Brake);
  }

  
  public double getInnerRPM(){
    return Falcon.ticksToRPM(innerFeeder.getSelectedSensorVelocity());
  }

  
  public double getOuterRPM(){
    return Falcon.ticksToRPM(outerFeeder.getSelectedSensorVelocity());
  }
  
  public void setInnerFeederRPM(double rpm){
    innerFeeder.set(TalonFXControlMode.Velocity, Falcon.rpmToTicks(rpm));
  }

  public void setOuterFeederRPM(double rpm){
    outerFeeder.set(TalonFXControlMode.Velocity, Falcon.rpmToTicks(rpm));
  }

  public boolean rejectBall(){
    if (DriverStation.getAlliance() == Alliance.Blue){
      return false; //color.red / saturation > 0.5 * saturation;
    }
    return false; //color.blue / saturation > 0.5 * saturation;
  }

  public void runRouting(boolean intakeOut){
    boolean ballInLower = !lowerBeambreak.get();
    boolean ballInUpper = !upperBeambreak.get();
    if(!ballInUpper){
      setInnerFeederRPM(1000);
    } else {
      setInnerFeederRPM(0);
    }

    if(intakeOut && !(ballInLower && ballInUpper)) {
      setOuterFeederRPM(2000);
    } else {
      setOuterFeederRPM(0);
    }
  }
  @Override
  public void periodic() {
    // This method will be called once per scheduler run

    // color = getColor();
    //saturation = color.red + color.blue + color.green;

    
  }
}
