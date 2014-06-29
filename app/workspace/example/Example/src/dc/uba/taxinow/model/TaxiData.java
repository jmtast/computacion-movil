package dc.uba.taxinow.model;

public class TaxiData {

	private String marca;
	private String modelo;
	private String patente;
	
	
	public TaxiData(String marca, String modelo, String patente) {
		super();
		this.marca = marca;
		this.modelo = modelo;
		this.patente = patente;
	}
	
	public TaxiData(String fullData){
		String[] list = fullData.split(",");
		marca = list[0];
		modelo = list[1];
		patente = list[2];
	}
	
	public String getMarca() {
		return marca;
	}
	public void setMarca(String marca) {
		this.marca = marca;
	}
	public String getModelo() {
		return modelo;
	}
	public void setModelo(String modelo) {
		this.modelo = modelo;
	}
	public String getPatente() {
		return patente;
	}
	public void setPatente(String patente) {
		this.patente = patente;
	}
	
	public String toString(){
		return marca+","+modelo+","+patente;
	}
	
}
