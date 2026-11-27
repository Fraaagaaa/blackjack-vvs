package blackjack;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.domain.JavaModifier;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.core.importer.ImportOption;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

class ArchitectureIT {
  private final JavaClasses code =
      new ClassFileImporter()
          .withImportOption(new ImportOption.DoNotIncludeTests())
          .importPackages("blackjack");

  @Test
  void actionsImplementActionContract() {
    classes()
        .that()
        .haveSimpleNameStartingWith("Action")
        .should()
        .implement(IAction.class)
        .check(code);
  }

  @Test
  void actionsDependOnDealerAbstraction() {
    noClasses()
        .that()
        .haveSimpleNameStartingWith("Action")
        .should()
        .dependOnClassesThat()
        .haveFullyQualifiedName(Dealer.class.getName())
        .check(code);
  }

  @Tag("known-bug")
  @Test
  void ARQUITECTURA001_consoleDependenciesAreConfinedToBoundaryClasses() {
    noClasses()
        .that()
        .doNotHaveSimpleName("BlackJackGame")
        .and()
        .doNotHaveSimpleName("PlayerHuman")
        .should()
        .dependOnClassesThat()
        .resideInAPackage("java.io..")
        .check(code);
  }

  @Test
  void reusableCardModelDoesNotDependOnConsole() {
    noClasses()
        .that()
        .haveNameMatching("blackjack\\.(Card|Hand|Deck)")
        .should()
        .dependOnClassesThat()
        .resideInAPackage("java.io..")
        .check(code);
  }

  @Test
  void playerBaseTypeIsAbstract() {
    classes()
        .that()
        .haveSimpleName("Player")
        .should()
        .haveModifier(JavaModifier.ABSTRACT)
        .check(code);
  }

  @Test
  void dealerImplementsDealerContract() {
    classes().that().haveSimpleName("Dealer").should().implement(IDealer.class).check(code);
  }

  @Test
  void dealerImplementsBankerContract() {
    classes().that().haveSimpleName("Dealer").should().implement(IBanker.class).check(code);
  }

  @Test
  void interfacesFollowProjectNamingConvention() {
    classes().that().areInterfaces().should().haveSimpleNameStartingWith("I").check(code);
  }

  @Test
  void productionDoesNotDependOnTestFrameworks() {
    noClasses()
        .should()
        .dependOnClassesThat()
        .resideInAnyPackage("org.junit..", "org.mockito..", "net.jqwik..")
        .check(code);
  }
}
