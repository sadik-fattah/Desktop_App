using Avalonia;
using Avalonia.Controls.ApplicationLifetimes;
using Avalonia.Data.Core;
using Avalonia.Data.Core.Plugins;
using Avalonia.Markup.Xaml;
using GetStartedApp.ViewModels;
using GetStartedApp.Views;

namespace GetStartedApp;

public partial class App : Application
{
    public override void Initialize()
    {
        AvaloniaXamlLoader.Load(this);
    }

    public override void OnFrameworkInitializationCompleted()
    {
         private DateTime time = DateTime.MinValue;
        if (ApplicationLifetime is IClassicDesktopStyleApplicationLifetime desktop)
        {
            // Line below is needed to remove Avalonia data validation.
            // Without this line you will get duplicate validations from both Avalonia and CT
             this.linkLabel1.Text = string.Format("http://{192.168.1.1}:8080", Environment.MachineName);
            BindingPlugins.DataValidators.RemoveAt(0);
            _Server = new ImageStreamingServer();
            _Server.Start(8080);
             private void timer1_Tick(object sender, EventArgs e)
        {
            int count = (_Server.Clients != null) ? _Server.Clients.Count() : 0;

            this.sts.Text = "Clients: " + count.ToString();
        }
            desktop.MainWindow = new MainWindow{
                DataContext = new MainWindowViewModel(),
                System.Diagnostics.Process.Start("firefox", this.linkLabel1.Text);
            };
        }

        base.OnFrameworkInitializationCompleted();
    }
}