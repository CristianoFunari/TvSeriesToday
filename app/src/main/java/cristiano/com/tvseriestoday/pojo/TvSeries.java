
package cristiano.com.tvseriestoday.pojo;

import java.util.HashMap;
import java.util.Map;


public class TvSeries {

    private String firstAired;
    private Episode episode;
    private Show show;
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    /**
     *
     * @return
     *     The firstAired
     */
    public String getFirstAired() {
        return firstAired;
    }

    /**
     *
     * @param firstAired
     *     The first_aired
     */
    public void setFirstAired(String firstAired) {
        this.firstAired = firstAired;
    }

    /**
     *
     * @return
     *     The episode
     */
    public Episode getEpisode() {
        return episode;
    }

    /**
     *
     * @param episode
     *     The episode
     */
    public void setEpisode(Episode episode) {
        this.episode = episode;
    }

    /**
     *
     * @return
     *     The show
     */
    public Show getShow() {
        return show;
    }

    /**
     *
     * @param show
     *     The show
     */
    public void setShow(Show show) {
        this.show = show;
    }

    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

}
