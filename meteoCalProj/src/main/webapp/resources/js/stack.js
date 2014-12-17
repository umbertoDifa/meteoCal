PrimeFaces.widget.Stack = PrimeFaces.widget.BaseWidget.extend({init: function (a) {
        this._super(a);
        this.cfg.expanded = this.cfg.expanded || false;
        var b = this;
        $(this.jqId + ".ui-stack > img").on("click.stack", function () {
            if (b.cfg.expanded) {
                b.collapse($(this))
            } else {
                b.open($(this))
            }
        });
        if (this.cfg.expanded) {
            this.open(this.jq.children("img"))
        }
    }, open: function (c) {
        var b = 0, a = 0, d = this;
        c.next().children().each(function () {
            $(this).animate({top: "-" + b + "px", right: a + "px"}, d.cfg.openSpeed);
            b = b + 55;
            a = (a + 0.75) * 2
        });
        c.next().animate({top: "-50px", left: "70px"}, this.cfg.openSpeed).addClass("openStack").find("li a>img").animate({width: "50px", marginRight: "9px"}, this.cfg.openSpeed);
        c.animate({paddingTop: "0"});
        this.cfg.expanded = true
    }, collapse: function (a) {
        a.next().removeClass("openStack").children("li").animate({top: "55px", right: "-10px"}, this.cfg.closeSpeed);
        a.next().find("li a>img").animate({width: "79px", marginRight: "0"}, this.cfg.closeSpeed);
        a.animate({paddingTop: "35"});
        this.cfg.expanded = false
    }});