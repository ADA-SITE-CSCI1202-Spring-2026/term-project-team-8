BASE=~/Desktop/ADA/term-project-team-8/AresBase
FX=/Users/fidankhalilova/Downloads/javafx-sdk-26/lib
MODS=javafx.controls,javafx.fxml,javafx.base,javafx.graphics

echo "Compiling..."
javac --module-path $FX --add-modules $MODS -d $BASE/out $(find $BASE/src -name "*.java")

if [ $? -eq 0 ]; then
    echo "Running..."
    java --module-path $FX --add-modules $MODS -cp $BASE/out aresbase.Main
else
    echo "Compilation failed."
fi
