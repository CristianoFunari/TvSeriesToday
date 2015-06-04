
package cristiano.com.tvseriestoday.pojo;

import java.util.HashMap;
import java.util.Map;

public class Episode {

    private Integer season;
    private Integer number;
    private String title;
    private Ids ids;
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    /**
     * 
     * @return
     *     The season
     */
    public Integer getSeason() {
        return season;
    }

    /**
     * 
     * @param season
     *     The season
     */
    public void setSeason(Integer season) {
        this.season = season;
    }

    /**
     * 
     * @return
     *     The number
     */
    public Integer getNumber() {
        return number;
    }

    /**
     * 
     * @param number
     *     The number
     */
    public void setNumber(Integer number) {
        this.number = number;
    }

    /**
     * 
     * @return
     *     The title
     */
    public String getTitle() {
        return title;
    }

    /**
     * 
     * @param title
     *     The title
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * 
     * @return
     *     The ids
     */
    public Ids getIds() {
        return ids;
    }

    /**
     * 
     * @param ids
     *     The ids
     */
    public void setIds(Ids ids) {
        this.ids = ids;
    }

    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

}
