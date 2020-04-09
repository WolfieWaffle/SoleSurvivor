package com.github.wolfiewaffle.solesurvivor.capability;

public class Temperature implements ITemperature {

	private double tempature;
	private double targetTempature;

	public Temperature() {

	}

	@Override
	public void setTemperature(double tempature) {
		this.tempature = tempature;
	}

	@Override
	public double getTemperature() {
		return tempature;
	}

	@Override
	public void setTargetTemperature(double targetTempature) {
		this.targetTempature = targetTempature;
	}

	@Override
	public double getTargetTemperature() {
		return targetTempature;
	}

}
