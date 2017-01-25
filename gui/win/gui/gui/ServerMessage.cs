using Newtonsoft.Json;
using System;
using System.Collections.Generic;
using System.IO;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace gui
{
    class ServerMessage
    {

        JsonTextReader jsonReader = null;
        String alertMsg = null;
        String msgType = null;


        public ServerMessage()
        { 
        
        }


        public String getMessage()
        {
            return alertMsg;
        }


        public String getLabel()
        {
            return msgType;
        }


        public bool Parse(String message)
        {
            jsonReader = new JsonTextReader(new StringReader(message));

            try
            {
                jsonReader.Read();
            }
            catch (JsonException)
            {
                return false;
            }

            String key = null, value = null;

            while (jsonReader.Read())
            {
                if (jsonReader.Value == null)
                    continue;

                if (jsonReader.TokenType == JsonToken.PropertyName)
                {
                    key = jsonReader.Value.ToString();
                    value = null;
                }
                if (jsonReader.TokenType == JsonToken.String)
                    value = jsonReader.Value.ToString();
                if (key != null && value != null)
                {
                    if (key.CompareTo("user_message") == 0)
                        this.alertMsg = value;
                    if (key.CompareTo("label") == 0)
                        this.msgType = value;

                    key = null;
                    value = null;
                }
            }

            return message != null && msgType != null;
        }

    }
}
