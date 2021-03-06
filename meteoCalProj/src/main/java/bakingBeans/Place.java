package bakingBeans;

import java.io.Serializable;
import javax.faces.view.ViewScoped;
import javax.inject.Named;

/**
 *
 * @author umboDifa
 */
@Named(value = "place")
@ViewScoped
public class Place implements Serializable {

    private String streetNumber;
    private String locality;
    private String route;
    private String country;
    private String postalCode;
    private String administrativeArea;

    public Place() {
    }

    public String getStreetNumber() {
        return streetNumber;
    }

    public void setStreetNumber(String streetNumber) {
        this.streetNumber = streetNumber;
    }

    public String getLocality() {
        return locality;
    }

    public void setLocality(String locality) {
        this.locality = locality;
    }

    public String getRoute() {
        return route;
    }

    public void setRoute(String route) {
        this.route = route;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public String getAdministrativeArea() {
        return administrativeArea;
    }

    public void setAdministrativeArea(String administrativeArea) {
        this.administrativeArea = administrativeArea;
    }

    @Override
    public String toString() {
        String toReturn = "";
        String separator = "";

        if (streetNumber != null && !streetNumber.isEmpty()) {
            toReturn += streetNumber;
            separator = ",";
        }
        if (locality != null && !locality.isEmpty()) {
            toReturn += separator + locality;
            separator = ",";
        }
        if (route != null && ! route.isEmpty()) {
            toReturn += separator + route;
            separator = ",";
        }
        if (country != null && !country.isEmpty()) {
            toReturn += separator + country;
            separator = ",";
        }
        if (postalCode != null && !postalCode.isEmpty()) {
            toReturn += separator + postalCode;
            separator = ",";
        }
        if (administrativeArea != null && !administrativeArea.isEmpty()) {
            toReturn += separator + administrativeArea;
        }

        return toReturn;
    }

}
