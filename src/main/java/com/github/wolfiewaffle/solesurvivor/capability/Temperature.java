package com.github.wolfiewaffle.solesurvivor.capability;

public class Temperature implements ITemperature {

	private double tempature;

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

}
