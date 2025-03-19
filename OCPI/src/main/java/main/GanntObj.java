package main;

public class GanntObj {


    //{ id: 1, content: 'Item 1', start: '2023-09-01', group: 1 },
    private long id;
    private final String content;
    private final String start;
    private final String end;
    private final String group;
    private final double value;
    private final String style;

    public GanntObj(long id, String content, String start, String end, String group, String style,double value) {
        this.id = id;
        this.content = content;
        this.start = start;
        this.end = end;
        this.group = group;
        this.value = value;
        this.style = style;
    }

    /**
     * @return the id
     */
    public long getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(long id) {
        this.id = id;
    }

    /**
     * @return the content
     */
    public String getContent() {
        return content;
    }

    /**
     * @return the start
     */
    public String getStart() {
        return start;
    }

    /**
     * @return the end
     */
    public String getEnd() {
        return end;
    }

    /**
     * @return the group
     */
    public String getGroup() {
        return group;
    }

    /**
     * @return the value
     */
    public double getValue() {
        return value;
    }

    /**
     * @return the style
     */
    public String getStyle() {
        return style;
    }    
}
