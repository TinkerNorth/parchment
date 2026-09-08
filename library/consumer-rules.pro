# Parchment consumer ProGuard rules
# These rules are applied to consumers of this library

# Keep all public API classes
-keep public class mobi.parchment.widget.adapterview.listview.ListView { *; }
-keep public class mobi.parchment.widget.adapterview.gridview.GridView { *; }
-keep public class mobi.parchment.widget.adapterview.gridpatternview.GridPatternView { *; }

# Keep custom view constructors (required for XML inflation)
-keepclasseswithmembers class mobi.parchment.widget.adapterview.** {
    public <init>(android.content.Context);
    public <init>(android.content.Context, android.util.AttributeSet);
    public <init>(android.content.Context, android.util.AttributeSet, int);
}

# Keep custom attributes
-keepclassmembers class **.R$styleable {
    public static <fields>;
}
