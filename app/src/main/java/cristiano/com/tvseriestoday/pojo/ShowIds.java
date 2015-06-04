
package cristiano.com.tvseriestoday.pojo;

import java.util.HashMap;
import java.util.Map;


public class ShowIds {

    private Integer trakt;
    private String slug;
    private Integer tvdb;
    private String imdb;
    private Integer tmdb;
    private Integer tvrage;
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    /**
     * 
     * @return
     *     The trakt
     */
    public Integer getTrakt() {
        return trakt;
    }

    /**
     * 
     * @param trakt
     *     The trakt
     */
    public void setTrakt(Integer trakt) {
        this.trakt = trakt;
    }

    /**
     * 
     * @return
     *     The slug
     */
    public String getSlug() {
        return slug;
    }

    /**
     * 
     * @param slug
     *     The slug
     */
    public void setSlug(String slug) {
        this.slug = slug;
    }

    /**
     * 
     * @return
     *     The tvdb
     */
    public Integer getTvdb() {
        return tvdb;
    }

    /**
     * 
     * @param tvdb
     *     The tvdb
     */
    public void setTvdb(Integer tvdb) {
        this.tvdb = tvdb;
    }

    /**
     * 
     * @return
     *     The imdb
     */
    public String getImdb() {
        return imdb;
    }

    /**
     * 
     * @param imdb
     *     The imdb
     */
    public void setImdb(String imdb) {
        this.imdb = imdb;
    }

    /**
     * 
     * @return
     *     The tmdb
     */
    public Integer getTmdb() {
        return tmdb;
    }

    /**
     * 
     * @param tmdb
     *     The tmdb
     */
    public void setTmdb(Integer tmdb) {
        this.tmdb = tmdb;
    }

    /**
     * 
     * @return
     *     The tvrage
     */
    public Integer getTvrage() {
        return tvrage;
    }

    /**
     * 
     * @param tvrage
     *     The tvrage
     */
    public void setTvrage(Integer tvrage) {
        this.tvrage = tvrage;
    }

    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

}
