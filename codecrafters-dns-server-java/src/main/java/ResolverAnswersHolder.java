import java.util.List;

class ResolverAnswersHolder {

    public final DnsMessage originalMessage;
    public final List<DnsAnswer> answers;

    public ResolverAnswersHolder(DnsMessage originalMessage, List<DnsAnswer> answers) {
        this.originalMessage = originalMessage;
        this.answers = answers;
    }

    public boolean isReadyToSendResponseToClient() {
        // don't know whether there will be answers > questions
        return originalMessage.questions.size() <= answers.size();
    }

    public void answersReceived(List<DnsAnswer> answers) {
        this.answers.addAll(answers);
    }

}
