package de.burnthelemon.ggnadditons.util;

import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;

public abstract class CustomFormattingTags {

   public static TagResolver getCustomTags() {
      return TagResolver.builder().resolvers(new TagResolver[]{
              Placeholder.parsed("coin", "\ue000"),
              Placeholder.parsed("half_coin", "\ue001"),
              Placeholder.parsed("gray_coin", "\ue002"),
              Placeholder.parsed("ggn", "\ue004"),
              Placeholder.parsed("globalchat", "\ue005"),
              Placeholder.parsed("localchat", "\ue006"),
              Placeholder.parsed("discord", "\ue007"),
              Placeholder.parsed("heart", "<white>\ue008</white>"),
              Placeholder.parsed("wb", "<white>\ue009</white>"),
              Placeholder.parsed("vineboom", "<hover:show_text:'<gray>Sound:<white>Vineboom</white></gray>'><click:run_command:/sound vineboom>\uE010</click></hover>"),

      }).build();
   }
}
