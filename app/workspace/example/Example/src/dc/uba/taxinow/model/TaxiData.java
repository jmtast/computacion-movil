package dc.uba.taxinow.model;

public class TaxiData {

	private String id;
	private String plate;
	private String brand;
	private String model;
	
	public TaxiData(String id, String plate, String brand, String model) {
		super();
		this.id = id;
		this.plate = plate;
		this.brand = brand;
		this.model = model;
	}
	
	public TaxiData(String fullData){
		String[] list = fullData.split(",");
		brand = list[0];
		model = list[1];
		plate = list[2];
	}
	
	public String getId() {
		return id;
	}
	public String getPlate() {
		return plate;
	}
	public String getBrand() {
		return brand;
	}
	public String getModel() {
		return model;
	}
	
	public String toString(){
		return brand+","+model+","+plate;
	}
	
}
